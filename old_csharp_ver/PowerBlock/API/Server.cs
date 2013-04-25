using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace PowerBlock.API
{
    public class Server
    {
        public static Level[] Levels
        {
            get
            {
                List<Level> TempList = new List<Level>();
                CraftLevel[] CraftLevelList = CraftServer.Levels;
                foreach (CraftLevel cl in CraftLevelList)
                    TempList.Add(new Level(cl.Name));
                return TempList.ToArray();
            }
        }

        public static int MaxPlayers { get { return CraftServer.MaxPlayers; } }
        public static Player[] Players { get { return CraftServer.Players; } }
        public static string Motd { get { return CraftServer.Motd; } }
        public static Level MainLevel { get { return new Level(CraftServer.MainLevel); } }
        public static bool AllowWOM { get { return CraftServer.AllowWOM; } }
        public static string Name { get { return CraftServer.Name; } }
        public static string Address { get { return CraftServer.Address; } }
        public static int Port { get { return CraftServer.Port; } }
        public static bool OnlineMode { get { return CraftServer.OnlineMode; } }
        public static bool Whitelist { get { return CraftServer.Whitelist; } }

        public static void SetIPBanned(string IP, bool Value)
        {

        }

        public static void DispatchCommand(string Command) { Program.ParseCommand(Command); }
        public static void SetBanned(string Name, bool Value) { CraftServer.GetPlayer(Name).Banned = Value; }   
    }
}
