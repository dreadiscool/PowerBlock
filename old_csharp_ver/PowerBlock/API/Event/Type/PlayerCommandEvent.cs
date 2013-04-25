using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace PowerBlock.API.Event.Type
{
    public class PlayerCommandEvent
    {
        private Player _Player;
        private string _Command;

        public PlayerCommandEvent(Player Player, string Command)
        {
            _Player = Player;
            _Command = Command;
        }

        public Player Player { get { return _Player; } }
        public string Command { get { return _Command; } }
    }
}
