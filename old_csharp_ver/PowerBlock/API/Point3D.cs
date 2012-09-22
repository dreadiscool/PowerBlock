using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace PowerBlock.API
{
    public class Point3D
    {
        public double X;
        public double Y;
        public double Z;

        public Point3D(double x, double y, double z)
        {
            X = x;
            Y = y;
            Z = z;
        }

        public void Add(double AddX, double AddY, double AddZ)
        {
            X += AddX;
            Y += AddY;
            Z += AddZ;
        }

        public override string ToString()
        {
            return "(" + X.ToString() + ", " + Y.ToString() + ", " + Z.ToString() + ")";
        }
    }
}
