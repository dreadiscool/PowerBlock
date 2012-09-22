using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using PowerBlock.API;
using System.Xml.Serialization;
using System.IO;
using System.IO.Compression;
using System.Net;

namespace PowerBlock
{
    class CraftLevel
    {
        private string _Name;
        private byte[] _LevelBlocks;
        private int _Length = 16;
        private int _Width = 16;
        private int _Height = 16;
        private Point3D _Spawn = new Point3D(16, 16, 16);

        public CraftLevel(string Name, int LevelLength, int LevelWidth, int LevelHeight)
        {
            Name = Name.Replace(CraftServer.Environment + CraftServer.DirStr + "Levels" + CraftServer.DirStr, "");
            if (LevelLength < 32)
                LevelLength = 32;
            if (LevelWidth < 32)
                LevelWidth = 32;
            if (LevelHeight < 32)
                LevelHeight = 32;
            _Length = LevelLength;
            _Width = LevelWidth;
            _Height = LevelHeight;
            _LevelBlocks = new byte[LevelLength * LevelWidth * LevelHeight];
            _Name = Name;
            for (int i = 0; i != (LevelLength * LevelWidth * LevelHeight); i++)
            {
                _LevelBlocks[i] = BlockType.Air;
            }
            for (int i = 0; i < (LevelLength * LevelWidth * LevelHeight * 0.5); i++)
            {
                _LevelBlocks[i] = BlockType.Dirt;
            }
            for (int i = (int)(LevelLength * LevelWidth * LevelHeight * 0.5); i < (LevelLength * LevelWidth * LevelHeight * 0.5 + (LevelLength * LevelWidth)); i++)
            {
                _LevelBlocks[i] = BlockType.Grass;
            }
            _Spawn = new Point3D(LevelLength / 2, LevelHeight / 2 + 1.4, LevelWidth / 2);
            Save();
            Console.WriteLine("Finished generating Level!");
            CraftServer.AddLevel(this);
        }

        public CraftLevel(string Name)
        {
            _Name = Name.Replace(CraftServer.Environment + CraftServer.DirStr + "Levels" + CraftServer.DirStr, "");
            Load();
        }

        public void Load()
        {
            try
            {
                StreamReader sr = new StreamReader(CraftServer.Environment + CraftServer.DirStr + "Levels" + CraftServer.DirStr + _Name + ".pbmap");
                _Length = Convert.ToInt32(sr.ReadLine());
                _Width = Convert.ToInt32(sr.ReadLine());
                _Height = Convert.ToInt32(sr.ReadLine());
                _LevelBlocks = new byte[_Length * _Width * _Height];
                for (int i = 0; i != (_Length * _Width * _Height); i++)
                {
                    _LevelBlocks[i] = (byte)Convert.ToInt32(sr.ReadLine());
                }
                sr.Close();
            }
            catch (Exception LevelError)
            {
                Console.WriteLine("Error while loading Level '" + _Name + "'\n" + LevelError.ToString());
            }
        }

        public void Save()
        {
            try
            {
                FileStream fs = new FileStream(CraftServer.Environment + CraftServer.DirStr + "Levels" + CraftServer.DirStr + _Name + ".pbmap", FileMode.Create);
                StreamWriter MapWriter = new StreamWriter(fs);
                MapWriter.WriteLine(_Length.ToString());
                MapWriter.WriteLine(_Width.ToString());
                MapWriter.WriteLine(_Height.ToString());
                for (int i = 0; i != _LevelBlocks.Length; i++)
                {
                    MapWriter.WriteLine(((int)_LevelBlocks[i]).ToString());
                }
                MapWriter.Flush();
                MapWriter.Close();
                fs.Close();
            }
            catch (Exception MapSaveException)
            {
                Console.WriteLine("Error while saving map data for '" + _Name + "'\n" + MapSaveException.ToString());
            }
        }

        public void AddToList()
        {
            CraftServer.AddLevel(this);
        }

        public void ServeLevel(CraftPlayer cp)
        {
            try
            {
                cp.Client.GetStream().WriteByte(0x02);
                int MapSizeBlocks = _Length * _Width * _Height;
                int ChunksNeeded = (int)Math.Ceiling((double)MapSizeBlocks / 1024);
                byte[] FullWorldData = new byte[MapSizeBlocks + 4];
                BitConverter.GetBytes(IPAddress.HostToNetworkOrder(MapSizeBlocks)).CopyTo(FullWorldData, 0);
                Buffer.BlockCopy(_LevelBlocks, 0, FullWorldData, 4, _LevelBlocks.Length);
                FullWorldData = GZip(FullWorldData);
                for (int i = 1; FullWorldData.Length > 0; i++)
                {
                    short EmbedLength = (short)Math.Min(FullWorldData.Length, 1024);
                    byte[] Package = new byte[1028];
                    Package[0] = 0x03;
                    ShortToByte(EmbedLength).CopyTo(Package, 1);
                    Buffer.BlockCopy(FullWorldData, 0, Package, 3, EmbedLength);
                    Package[1027] = (byte)((i / ChunksNeeded) * 100);
                    byte[] Cache = new byte[FullWorldData.Length - EmbedLength];
                    Buffer.BlockCopy(FullWorldData, EmbedLength, Cache, 0, FullWorldData.Length - EmbedLength);
                    FullWorldData = Cache;
                    cp.Client.GetStream().Write(Package, 0, Package.Length);
                }
                cp.Client.GetStream().WriteByte(0x04);
                cp.Client.GetStream().Write(ShortToByte((short)_Length), 0, 2);
                cp.Client.GetStream().Write(ShortToByte((short)_Height), 0, 2);
                cp.Client.GetStream().Write(ShortToByte((short)_Width), 0, 2);
            }
            catch (Exception MapSendException)
            {
                Console.WriteLine("Error while serving map '" + _Name + "' to Player '" + cp.Username + "'!\n" + MapSendException.ToString());
                cp.KickPlayer("Internal error - could not serve map", Response.KickReason.INTERNAL_SERVER_ERROR);
            }
            byte[] SpawnPacket = new byte[74];
            SpawnPacket[0] = 0x07;
            SpawnPacket[1] = cp.ID;
            cp.StringToByte(cp.Username).CopyTo(SpawnPacket, 2);
            ShortToByte((short)(_Spawn.X * 32)).CopyTo(SpawnPacket, 66);
            ShortToByte((short)(_Spawn.Y * 32)).CopyTo(SpawnPacket, 68);
            ShortToByte((short)(_Spawn.Z * 32)).CopyTo(SpawnPacket, 70);
            SpawnPacket[72] = cp.Yaw;
            SpawnPacket[73] = cp.Pitch;
            foreach (CraftPlayer cplayer in CraftServer.CraftPlayers)
            {
                try
                {
                    if (cplayer.LevelIn == cp.LevelIn)
                    {
                        if (cplayer.Username == cp.Username)
                        {
                            unchecked { SpawnPacket[1] = (byte)-1; }
                        }
                        else
                        {
                            SpawnPacket[1] = cp.ID;
                        }
                        cplayer.Client.GetStream().Write(SpawnPacket, 0, 74);
                    }
                }
                catch { cplayer.Disconnect(); }
            }
            foreach (CraftPlayer spawning in CraftServer.CraftPlayers)
            {
                if (spawning.LevelIn == cp.LevelIn && spawning.Username != cp.Username)
                {
                    byte[] Update = new byte[74];
                    Update[0] = 0x07;
                    Update[1] = spawning.ID;
                    spawning.StringToByte(spawning.Username).CopyTo(Update, 2);
                    ShortToByte((short)(spawning.Position.X * 32)).CopyTo(Update, 66);
                    ShortToByte((short)(spawning.Position.Y * 32)).CopyTo(Update, 68);
                    ShortToByte((short)(spawning.Position.Z * 32)).CopyTo(Update, 70);
                    Update[72] = spawning.Yaw;
                    Update[73] = spawning.Pitch;
                    try { cp.Client.GetStream().Write(Update, 0, 74); }
                    catch { cp.Disconnect(); }
                }
            }
        }

        public byte GetBlock(int X, int Y, int Z)
        {
            try { return _LevelBlocks[PosToInt(X, Y, Z)]; }
            catch { return BlockType.Air; }
        }

        public void SetBlock(int X, int Y, int Z, byte Type)
        {
            if (X > _Length || Y > _Height || Z > _Width || X < 0 || Y < 0 || Z < 0)
                return;
            _LevelBlocks[PosToInt(X, Y, Z)] = Type;
            byte[] Package = new byte[8];
            Package[0] = 0x06;
            Buffer.BlockCopy(ShortToByte((short)X), 0, Package, 1, 2);
            Buffer.BlockCopy(ShortToByte((short)Y), 0, Package, 3, 2);
            Buffer.BlockCopy(ShortToByte((short)Z), 0, Package, 5, 2);
            Package[7] = Type;
            foreach (CraftPlayer cp in CraftServer.CraftPlayers)
            {
                if (cp.LevelIn == _Name)
                {
                    try
                    {
                        cp.Client.GetStream().Write(Package, 0, 8);
                    }
                    catch (Exception MapPacketError)
                    {
                        cp.KickPlayer("Internal error - could not update map!", Response.KickReason.INTERNAL_SERVER_ERROR);
                        Console.WriteLine("[ERROR] Could not send map data to '" + cp.Username + "'\n" + MapPacketError.ToString());
                    }
                }
            }
            Update();
        }

        public void SetCube(int BottomX, int BottomY, int BottomZ, int TopX, int TopY, int TopZ, byte NewBlock)
        {
            int StartPos = PosToInt(BottomX, BottomY, BottomZ);
            int FinishPos = PosToInt(TopX, TopY, TopZ);
            for (int i = StartPos; i < FinishPos; i++)
            {
                Point3D CurrPos = IntToPos(i);
                SetBlock((int)CurrPos.X, (int)CurrPos.Y, (int)CurrPos.Z, NewBlock);
            }
            Update();
        }

        public void Update()
        {
            CraftServer.RemoveLevel(_Name);
            CraftServer.AddLevel(this);
        }

        public int PosToInt(int x, int y, int z)
        {
            return y * (_Length * _Width) + (z * _Length) + x;
        }

        public Point3D IntToPos(int pos)
        {
            double x;
            double y;
            double z;
            int LeftOver = pos % (_Length * _Width);
            y = (double)(pos - LeftOver) / (_Length * _Width);
            int LeftOver2 = LeftOver % _Length;
            z = (double)(LeftOver - LeftOver2) / _Length;
            x = (double)LeftOver - (z * _Length);
            return new Point3D(x, y, z);
        }

        public byte[] ShortToByte(short x)
        {
            byte[] y = BitConverter.GetBytes(x); Array.Reverse(y); return y;
        }

        public byte[] UShortToByte(ushort x)
        {
            byte[] y = BitConverter.GetBytes(x); Array.Reverse(y); return y;
        }

        public byte[] GZip(byte[] bytes)
        {
            using (System.IO.MemoryStream ms = new System.IO.MemoryStream())
            {
                GZipStream gs = new GZipStream(ms, CompressionMode.Compress, true);
                gs.Write(bytes, 0, bytes.Length);
                gs.Close();
                ms.Position = 0;
                bytes = new byte[ms.Length];
                ms.Read(bytes, 0, (int)ms.Length);
                ms.Close();
                ms.Dispose();
            }
            return bytes;
        }
        public byte[] Decompress(byte[] gzip)
        {
            using (GZipStream stream = new GZipStream(new MemoryStream(gzip), CompressionMode.Decompress))
            {
                const int size = 4096;
                byte[] buffer = new byte[size];
                using (MemoryStream memory = new MemoryStream())
                {
                    int count = 0;
                    do
                    {
                        count = stream.Read(buffer, 0, size);
                        if (count > 0)
                        {
                            memory.Write(buffer, 0, count);
                        }
                    }
                    while (count > 0);
                    return memory.ToArray();
                }
            }
        }

        public byte GetAvailableID()
        {
            for (int i = 0; i < 255; i++)
            {
                bool Found = false;
                foreach (CraftPlayer cp in CraftServer.CraftPlayers)
                {
                    if (cp.ID == (byte)i)
                    {
                        Found = true;
                        break;
                    }
                }
                if (Found != true)
                    return (byte)i;
            }
            return unchecked((byte)-1);
        }

        public Point3D Spawn
        {
            get { return _Spawn; }
            set { _Spawn = value; }
        }

        public int Length { get { return _Length; } }
        public int Width { get { return _Width; } }
        public int Height { get { return _Height; } }
        public string Name { get { return _Name; } }
    }
}
