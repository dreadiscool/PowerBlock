using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace PowerBlock.API
{
    public interface PowerBlockPlugin
    {
        string Name();
        string Version();
        string Author();
        void Enable();
        void Disable();
    }
}
