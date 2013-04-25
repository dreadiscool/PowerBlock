using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using System.Net;
using System.Threading;
//U see this? Do u see this?
namespace PowerBlock
{
    class Program
    {
        private static bool _Running = true;

        static void Main(string[] args)
        {
            Console.SetOut(CraftServer.ConsoleWriter);
            Console.Title = "PowerBlock - Release 0.1";
            Console.WriteLine("Server started!");
            ShowWatermark();
            CraftServer.PassArgs(args);
            CraftServer.Load();
            Console.WriteLine("Current date is " + DateTime.Now.ToString());
            new HeartBeat();
            Thread AsyncTask = new Thread(new ThreadStart(AsyncWaiterThread));
            AsyncTask.Start();
            while (_Running == true)
            {
                try
                {
                    string Command = Console.ReadLine();
                    ParseCommand(Command);
                }
                catch
                {
                    Console.WriteLine("[WARNING] Exception in main thread. Ignoring...");
                }
            }
        }

        public static void ParseCommand(string Command)
        {
            if (Command.ToLower() == "kill")
                System.Diagnostics.Process.GetCurrentProcess().Kill();
            else if (Command.ToLower() == "stop")
            {
                Console.WriteLine("Stopping server...");
                CraftServer.Unload();
                Console.WriteLine("Server stopped");
                foreach (CraftPlayer cp in CraftServer.CraftPlayers)
                    cp.KickPlayer("The server is shutting down!");
                _Running = false;
                ParseCommand("kill");
            }
            else if (Command.ToLower() == "reload")
            {
                Console.Write("Reloading all data...");
                CraftServer.Unload();
                CraftServer.Load();
                Console.WriteLine("Finished reloading!");
            }
            else if (Command.Split(' ')[0].ToLower() == "wgen")
            {
                try
                {
                    string[] Split = Command.Split(' ');
                    CraftLevel ToAdd = new CraftLevel(Split[1], Convert.ToInt32(Split[2]), Convert.ToInt32(Split[3]), Convert.ToInt32(Split[4]));
                }
                catch (Exception WorldGenException)
                {
                    Console.WriteLine(WorldGenException.Message);
                }
            }
            else if (Command.ToLower() == "plugins")
            {
                string Message = "Server Plugins: ";
                foreach (CraftPlugin cp in CraftServer.Plugins)
                {
                    if (Message == "Server Plugins: ")
                        Message = "Server Plugins: " + cp.Name;
                    else
                        Message += ", " + cp.Name;
                }
                Console.WriteLine(Message);
            }
            else if (Command.ToLower() == "list")
            {
                string Final = "";
                API.Player[] TempList = CraftServer.Players;
                foreach (API.Player p in TempList)
                {
                    if (Final == "")
                        Final = p.Username;
                    else
                        Final += ", " + p.Username;
                }
                if (Final != "")
                {
                    CraftServer.ConsoleWriter.WriteNoTimestamp(Final);
                    CraftServer.ConsoleWriter.WriteNoTimestamp("There are " + TempList.Length.ToString() + " out of a maximum " + CraftServer.MaxPlayers.ToString() + " players");
                }
                else
                    Console.WriteLine("There are no players online!");
            }
            else if (Command.ToLower() == "credits")
            {
                WebClient CreditLoader = new WebClient();
                Console.Write("Downloading CREDITS.TXT from Dropbox...");
                try { CraftServer.ConsoleWriter.WriteNoTimestamp(CreditLoader.DownloadString("http://dl.dropbox.com/u/29419562/powerblock-data/credits.txt").Replace(Environment.NewLine, "\n")); }
                catch { CraftServer.ConsoleWriter.WriteNoTimestamp("Failed to download CREDITS.TXT from Dropbox"); }
            }
            else if (Command.ToLower() == "help" || Command == "?")
            {
                WebClient HelpLoader = new WebClient();
                Console.Write("Downloading HELP.TXT from Dropbox...");
                try { CraftServer.ConsoleWriter.WriteNoTimestamp(HelpLoader.DownloadString("http://dl.dropbox.com/u/29419562/powerblock-data/help.txt").Replace(Environment.NewLine, "\n")); }
                catch { CraftServer.ConsoleWriter.WriteNoTimestamp("Failed to download HELP.TXT from Dropbox          "); }
            }
            else
            {
                if (CraftEvent.CraftServerCommandEvent(Command) == false)
                {
                    if (Command.ToLower().StartsWith("op"))
                    {
                        CraftPlayer Player = CraftServer.GetPlayer(Command.Split(' ')[1]);
                        bool AlreadyOp = Player.Op;
                        if (AlreadyOp == true)
                        {
                            Console.WriteLine("Deopping " + Player.Username);
                            Player.SetOp(false);
                            Player.SendMessage("&eYou are no longer op!");
                        }
                        else
                        {
                            Console.WriteLine("Opping " + Player.Username);
                            Player.SetOp(true);
                            Player.SendMessage("&eYou are now op!");
                        }
                    }
                    else if (Command.ToLower().StartsWith("kick"))
                    {
                        CraftServer.GetPlayer(Command.Split(' ')[1]).KickPlayer("You made an admin sad :(");
                    }
                    else if (Command.ToLower().StartsWith("ban"))
                    {
                        CraftServer.GetPlayer(Command.Split(' ')[1]).Banned = true;
                    }
                    else if (Command.ToLower().StartsWith("banip"))
                    {

                    }
                    else if (Command.ToLower().StartsWith("say"))
                    {
                        CraftServer.Broadcast("&d[Server] " + Command.Replace(Command.Split(' ')[0] + " ", ""));
                        Console.WriteLine("[Server] " + Command.Replace(Command.Split(' ')[0] + " ", ""));
                    }
                    else if (Command.ToLower() == "levels")
                    {
                        string Final = "Available Levels: ";
                        foreach (CraftLevel cl in CraftServer.Levels)
                        {
                            if (Final == "Available Levels: ")
                                Final += cl.Name;
                            else
                                Final += ", " + cl.Name;
                        }
                        CraftServer.ConsoleWriter.WriteNoTimestamp(Final);
                    }
                    else
                        Console.WriteLine("'" + Command + "' is not a valid command. Type 'help' or '?'");
                }
            }
        }

        static void ShowWatermark()
        {
            try
            {
                WebClient wc = new WebClient();
                string Watermark = wc.DownloadString("http://dl.dropbox.com/u/29419562/powerblock-data/boot.txt");
                CraftServer.ConsoleWriter.WriteNoTimestamp(Watermark.Replace(System.Environment.NewLine, "\n"));
            }
            catch
            {
                Console.WriteLine("By dreadiscool");
                Console.WriteLine("Licensed under GPL v3");
            }
        }

        static void AsyncWaiterThread()
        {
            long SecondsPassed = 1;
            while (true)
            {
                if (_Running == false)
                    return;
                foreach (CraftPlayer cp in CraftServer.CraftPlayers)
                {
                    try
                    {
                        cp.Client.GetStream().WriteByte(0x01);
                    }
                    catch (Exception excep)
                    {
                        cp.Disconnect();
                        Console.WriteLine(" :" + excep.Message);
                    }
                }
                if (SecondsPassed % 15 == 0) //send every 15 seconds so that player count is updated more often
                {
                    new HeartBeat();
                }
                if (SecondsPassed % 180 == 0)
                {
                    foreach (CraftLevel cl in CraftServer.Levels)
                        cl.Save();
                }
                if (SecondsPassed == CraftServer.RestartTimeSeconds)
                {
                    Console.WriteLine("Scheduler says it's time to stop!");
                    ParseCommand("stop");
                }
                SecondsPassed++;
                System.Threading.Thread.Sleep(1000);
            }
        }

        public static bool Running { get { return _Running; } }
    }
}
