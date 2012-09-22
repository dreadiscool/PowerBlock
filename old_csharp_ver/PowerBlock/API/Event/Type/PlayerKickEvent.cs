using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace PowerBlock.API.Event.Type
{
    public class PlayerKickEvent
    {
        private Player _Player;
        private string _KickMessage;
        private Response.KickReason _Reason;

        public PlayerKickEvent(Player cp, string Message, Response.KickReason Reason)
        {
            _Player = cp;
            _KickMessage = Message;
            _Reason = Reason;
        }

        public Player Player { get { return _Player; } }
        public string KickMessage { get { return _KickMessage; } }
        public Response.KickReason Reason { get { return _Reason; } }
    }
}
