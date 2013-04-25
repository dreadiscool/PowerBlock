using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace PowerBlock.API.Event.Type
{
    public class PlayerLeaveEvent
    {
        private Player _Player;

        public PlayerLeaveEvent(Player p)
        {
            _Player = p;
        }

        public Player Player { get { return _Player; } }
    }
}
