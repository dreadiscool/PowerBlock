using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace PowerBlock.API
{
    public class Player
    {
        private string _Username = "Player";

        public Player(string Username)
        {
            _Username = Username;
        }

        public void ServeMap(Level l)
        {
            CraftServer.GetPlayer(_Username).SendMap(l.Name);
        }

        public void SendMessage(string Message)
        {
            CraftServer.GetPlayer(_Username).SendMessage(Message);
        }

        public void KickPlayer(string Message, Response.KickReason Reason)
        {
            CraftServer.GetPlayer(_Username).KickPlayer(Message, Reason);
        }

        public void Teleport(Point3D Position, byte Yaw, byte Pitch)
        {
            CraftServer.GetPlayer(_Username).Teleport(Position, Yaw, Pitch);
        }

        public void Teleport(Point3D Position)
        {
            Teleport(Position, 0, 0);
        }

        public void KickPlayer() { KickPlayer("Kicked from server!"); }
        public void KickPlayer(string Message) { CraftServer.GetPlayer(_Username).KickPlayer(Message, Response.KickReason.UNSPECIFIED); }

        public string Username { get { return _Username; } }
        public Level LevelIn { get { return new Level(CraftServer.GetPlayer(_Username).LevelIn); } }
        public Point3D Position { get { return CraftServer.GetPlayer(_Username).Position; } }
        public string Address { get { return CraftServer.GetPlayer(_Username).Address; } }
        public bool Disconnected { get { return CraftServer.GetPlayer(_Username).Disconnected; } }
    }
}
