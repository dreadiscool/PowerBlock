using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace PowerBlock.API
{
    public class Level
    {
        private string _Name;

        public Level(string Name)
        {
            _Name = Name;
        }

        public byte GetBlock(int x, int y, int z)
        {
            return CraftServer.GetLevel(_Name).GetBlock(x, y, z);
        }

        public void SetBlock(int x, int y, int z, byte Block)
        {
            CraftServer.GetLevel(_Name).SetBlock(x, y, z, Block);
        }

        public string Name { get { return _Name; } }
    }
}
