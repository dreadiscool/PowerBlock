using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace PowerBlock.API.Event.Type
{
    public class PlayerMoveEvent
    {
        private Player _Player;
        private Point3D _RequestedLocation;
        private byte _RequestedPitch;
        private byte _RequestedYaw;

        public PlayerMoveEvent(Player Player, Point3D ReqLocation, byte ReqPitch, byte ReqYaw)
        {
            _Player = Player;
            _RequestedLocation = ReqLocation;
            _RequestedPitch = ReqPitch;
            _RequestedYaw = ReqYaw;
        }

        public Player Player { get { return _Player; } }
        public Point3D RequestedLocation { get { return _RequestedLocation; } }
        public byte RequestedPitch { get { return _RequestedPitch; } }
        public byte RequestedYaw { get { return _RequestedYaw; } }
    }
}
