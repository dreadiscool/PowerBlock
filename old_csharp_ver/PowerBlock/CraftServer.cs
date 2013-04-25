using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using System.Net;
using System.Net.Sockets;
using System.Reflection;
using System.Threading;

namespace PowerBlock
{
    class CraftServer
    {
        private static bool _Debugging = true;
        private static bool _LoadedOnce = false;
        private static string _Salt = "wo6kVAHjxoJcInKx";
        private static string _Environment = System.Environment.CurrentDirectory;
        private static string _Motd = "Welcome to my server!";
        private static string _Name = "PowerBlock Server";
        private static string _MainLevel = "world";
        private static string _Address = "";
        private static int _Port = 25565;
        private static int _MaxPlayers = 27;
        private static bool _OnlineMode = true;
        private static bool _Whitelist = false;
        private static bool _AllowWOM = true;
        private static List<CraftLevel> _Levels = new List<CraftLevel>();
        private static List<CraftPlugin> _Plugins = new List<CraftPlugin>();
        private static volatile List<CraftPlayer> _Clients = new List<CraftPlayer>();
        private static Writer _ConsoleWriter = new Writer();
        private static TcpListener RequestListener;
        private static long _RestartTimeSeconds = -1;

        public static void Load()
        {
            try
            {
                if (Directory.Exists(_Environment + CraftServer.DirStr + "Plugins" + CraftServer.DirStr) == false)
                    Directory.CreateDirectory(_Environment + CraftServer.DirStr + "Plugins" + CraftServer.DirStr);
                if (Directory.Exists(_Environment + CraftServer.DirStr + "Levels" + CraftServer.DirStr) == false)
                    Directory.CreateDirectory(_Environment + CraftServer.DirStr + "Levels" + CraftServer.DirStr);
                if (Directory.Exists(_Environment + CraftServer.DirStr + "Players" + CraftServer.DirStr) == false)
                    Directory.CreateDirectory(_Environment + CraftServer.DirStr + "Players" + CraftServer.DirStr);
                StreamReader ConfigReader = new StreamReader(System.Environment.CurrentDirectory + CraftServer.DirStr + "server.properties");
                while (ConfigReader.EndOfStream == false)
                {
                    string Input = ConfigReader.ReadLine();
                    string Property = Input.Split('=')[0].Trim().ToLower();
                    string Value = Input.Split('=')[1].Trim();
                    if (Property == "level-name")
                        _MainLevel = Value;
                    else if (Property == "server-port")
                        _Port = Convert.ToInt32(Value);
                    else if (Property == "server-ip")
                        _Address = Value;
                    else if (Property == "server-name")
                        _Name = Value;
                    else if (Property == "allow-wom")
                        _AllowWOM = Convert.ToBoolean(Value);
                    else if (Property == "whitelist")
                        _Whitelist = Convert.ToBoolean(Value);
                    else if (Property == "online-mode")
                        _OnlineMode = Convert.ToBoolean(Value);
                    else if (Property == "max-players")
                        _MaxPlayers = Convert.ToInt32(Value);
                    else if (Property == "motd")
                        _Motd = Value;
                }
            }
            catch
            {
                Console.WriteLine("Failed to read from configuration file!");
                Console.Write("Downloading SERVER.PROPERTIES from Dropbox...");
                System.Net.WebClient wc = new System.Net.WebClient();
                wc.DownloadFile("http://dl.dropbox.com/u/29419562/powerblock-data/server-properties.txt", _Environment + CraftServer.DirStr + "server.properties");
                _ConsoleWriter.WriteNoTimestamp("Downloaded file! Reloading...");
                Load();
            }
            GenerateSalt();
            LoadLevels();
            LoadPlugins();
            if (_LoadedOnce == false)
            {
                _LoadedOnce = true;
                try
                {
                    RequestListener = new TcpListener(IPAddress.Parse(_Address), _Port);
                    RequestListener.Start();
                    Thread AsyncListen = new Thread(new ThreadStart(IncomingConnectionListener));
                    AsyncListen.Start();
                }
                catch (Exception NetworkException)
                {
                    Console.WriteLine("Unable to listen for clients!\n" + NetworkException.ToString());
                    Console.WriteLine("Try 'reload' to retry listening for clients.");
                    _LoadedOnce = false;
                }
            }
        }

        public static void Unload()
        {
            UnloadPlugins();
            UnloadLevels();
        }

        public static void LoadPlugins()
        {
            Console.WriteLine("Loading plugins...");
            foreach (string Filename in Directory.GetFiles(_Environment + CraftServer.DirStr + "Plugins" + CraftServer.DirStr, "*.dll"))
            {
                Assembly PluginAssembly = Assembly.LoadFile(Filename);
                API.PowerBlockPlugin Main = null;
                API.Event.Listener.CraftListener Listen = null;
                foreach (Type PluginAssmeblyType in PluginAssembly.GetTypes())
                {
                    if (PluginAssmeblyType.GetInterface("PowerBlockPlugin") != null)
                        Main = (API.PowerBlockPlugin)Activator.CreateInstance(PluginAssmeblyType);
                    if (PluginAssmeblyType.GetInterface("CraftListener") != null)
                        Listen = (API.Event.Listener.CraftListener)Activator.CreateInstance(PluginAssmeblyType);
                }
                Main.Enable();
                if (Main != null && Listen != null)
                {
                    CraftPlugin NewPlugin = new CraftPlugin(Main, Listen);
                    Console.WriteLine(NewPlugin.PluginMain.Name() + " enabling..");
                    try { NewPlugin.PluginMain.Enable(); }
                    catch (Exception excep)
                    {
                        Console.WriteLine("[WARNING] Exception while loading " + NewPlugin.PluginMain.Name() + "!\n" + excep.ToString());
                    }
                    _Plugins.Add(NewPlugin);
                }
            }
            Console.WriteLine("Loaded all plugins!");
        }

        public static void UnloadPlugins()
        {
            Console.WriteLine("Disabling all plugins...");
            foreach (CraftPlugin cp in _Plugins)
            {
                Console.WriteLine(cp.Name + " is disabling...");
                cp.PluginMain.Disable();
            }
            Console.WriteLine("All plugins disabled!");
            _Plugins.Clear();
        }

        public static void LoadLevels()
        {
            bool FoundMain = false;
            Console.WriteLine("Loading level data...");
            foreach (string Filename in Directory.GetFiles(_Environment + CraftServer.DirStr + "Levels" + CraftServer.DirStr, "*.pbmap"))
            {
                CraftLevel Loaded = new CraftLevel(Filename.Replace(".pbmap", ""));
                Console.WriteLine("Loaded level '" + Loaded.Name.Replace(_Environment + CraftServer.DirStr + "Levels" + CraftServer.DirStr, "") + "'");
                if (Loaded.Name == MainLevel)
                    FoundMain = true;
                _Levels.Add(Loaded);
            }
            Console.WriteLine("Loaded all level data!");
            if (FoundMain == false)
            {
                new CraftLevel(MainLevel, 64, 64, 64);
            }
        }

        public static void UnloadLevels()
        {
            Console.WriteLine("Saving level data...");
            foreach (CraftLevel cl in _Levels)
            {
                cl.Save();
                Console.WriteLine("Saved level '" + cl.Name + "'");
            }
            Console.WriteLine("Saved all level data!");
            _Levels.Clear();
        }

        public static void IncomingConnectionListener()
        {
            try
            {
                while (Program.Running)
                {
                    if (Program.Running)
                        new CraftPlayer(RequestListener.AcceptTcpClient());
                    else
                        break;
                }
            }
            catch
            {
                Console.WriteLine("Stopped listening for clients");
            }
        }

        public static void AddPlayer(CraftPlayer cp)
        {
            _Clients.Add(cp);
        }

        public static void Broadcast(string Message)
        {
            foreach (CraftPlayer cp in CraftPlayers)
            {
                cp.SendMessage(Message);
            }
        }

        public static void CleanupPlayer(string Name)
        {
            CleanupPlayer(GetPlayer(Name));
            
        }

        public static void CleanupPlayer(CraftPlayer cp)
        {
            lock (_Clients)
            {
                _Clients.Remove(cp);
            }

        }

        public static PowerBlock.API.Player[] Players
        {
            get
            {
                List<PowerBlock.API.Player> TempList = new List<API.Player>();
                List<CraftPlayer> CraftTempList = new List<CraftPlayer>();
                lock (_Clients)
                    CraftTempList = _Clients;
                foreach (CraftPlayer cp in CraftTempList)
                {
                    TempList.Add(new PowerBlock.API.Player(cp.Username));
                }
                return TempList.ToArray();
            }
        }

        public static CraftPlayer[] CraftPlayers
        {
            get
            {
                lock (_Clients)
                {
                    return _Clients.ToArray();
                }
            }
        }

        public static CraftPlayer GetPlayer(string Username)
        {
            List<CraftPlayer> TempList = new List<CraftPlayer>();
            lock (_Clients)
                TempList = _Clients;
            foreach (CraftPlayer cp in TempList)
            {
                if (cp.Username.ToLower() == Username.ToLower())
                    return cp;
            }
            return null;
        }

        public static CraftLevel GetLevel(string Name)
        {
            lock (_Levels)
            {
                foreach (CraftLevel cl in _Levels)
                {
                    if (cl.Name.ToLower() == Name.ToLower())
                        return cl;
                }
            }
            return null;
        }

        public static void AddLevel(CraftLevel ToAdd)
        {
            lock (_Levels) { _Levels.Add(ToAdd); }
        }

        public static void RemoveLevel(string Name)
        {
            lock (_Levels) { _Levels.Remove(GetLevel(Name)); }
        }

        public static void GenerateSalt()
        {
            StringBuilder SaltBuilder = new StringBuilder();
            Random r = new Random();
            char ch;
            for (int i = 0; i < 16; i++)
            {
                ch = Convert.ToChar(Convert.ToInt32(Math.Floor(26 * r.NextDouble() + 65)));
                SaltBuilder.Append(ch);
            }
            _Salt = SaltBuilder.ToString();
        }

        public static void PassArgs(string[] args)
        {
            try
            {
                for (int i = 0; i < args.Length; i++)
                {
                    if (args[i].StartsWith("-"))
                    {
                        args[i] = args[i].ToLower().Replace("-", "");
                        if (args[i] == "environment")
                            _Environment = args[i + 1];
                        if (args[i] == "schedule.restart")
                            _RestartTimeSeconds = long.Parse(args[i + 1]);
                        i++;
                    }
                    else
                    {

                    }
                }
            }
            catch (Exception CommandLineException)
            {
                Console.WriteLine("Failed to parse argument!\n" + CommandLineException.ToString());
            }
        }

        public static bool Debugging { get { return _Debugging; } }
        public static string Salt { get { return _Salt; } }
        public static string Environment { get { return _Environment; } }
        public static string Motd { get { return _Motd; } }
        public static string Name { get { return _Name; } }
        public static string MainLevel { get { return _MainLevel; } }
        public static string Address { get { return _Address; } }
        public static int Port { get { return _Port; } }
        public static int MaxPlayers { get { return _MaxPlayers; } }
        public static bool OnlineMode { get { return _OnlineMode; } }
        public static bool Whitelist { get { return _Whitelist; } }
        public static bool AllowWOM { get { return _AllowWOM; } }
        public static CraftPlugin[] Plugins { get { return _Plugins.ToArray(); } }
        public static CraftLevel[] Levels { get { return _Levels.ToArray(); } }
        public static Writer ConsoleWriter { get { return _ConsoleWriter; } }
        public static long RestartTimeSeconds { get { return _RestartTimeSeconds; } }

        public static string DirStr { get { return Path.DirectorySeparatorChar.ToString(); } }
    }
}
