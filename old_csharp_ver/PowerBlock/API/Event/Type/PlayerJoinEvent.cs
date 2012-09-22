using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace PowerBlock.API.Event.Type
{
    public class PlayerJoinEvent
    {
        private Player _Player;

        public PlayerJoinEvent(Player Player)
        {
            _Player = Player;
        }

        public Player Player { get { return _Player; } }
    }
}
