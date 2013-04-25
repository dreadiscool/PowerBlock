using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace PowerBlock.API.Event.Type
{
    public class PlayerChatEvent
    {
        private Player _Player;
        private string _Message;
        private string _Format;

        public PlayerChatEvent(Player Player, string Message)
        {
            _Player = Player;
            _Message = Message;
        }

        public Player Player { get { return _Player; } }
        public string Message { get { return _Message; } }
        public string Format { get { return "<&name> &msg"; } }
    }
}
