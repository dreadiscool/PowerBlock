using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net.Sockets;
using System.Threading;
using PowerBlock.API;
using System.IO;
using System.Security.Cryptography;

namespace PowerBlock
{
    class CraftPlayer
    {
        private static ASCIIEncoding Encoder = new ASCIIEncoding();
        private static MD5CryptoServiceProvider Hasher = new MD5CryptoServiceProvider();

        private TcpClient _Client;
        private bool _Disconnected = false;
        private byte _ID = 0;
        private string _Username = "Player";
        private string _LevelIn = CraftServer.MainLevel;
        private byte _Pitch; //up and down
        private byte _Yaw; //left vs right
        private Point3D _Position = null;
        private bool _Op = false;
        private bool _Banned = false;
        private bool _FlipHead = false;

        public CraftPlayer(TcpClient Client)
        {
            _Client = Client;
            Thread thread = new Thread(new ThreadStart(StreamListener));
            thread.Start();
        }

        public void StreamListener()
        {
            try
            {
                if (_Client.GetStream().ReadByte() == (byte)0x00)
                {
                    byte[] LoginData = new byte[130];
                    _Client.GetStream().Read(LoginData, 0, 130);
                    if (LoginData[0] != 0x07)
                    {
                        KickPlayer("Incorrect protocol version! (0x07)");
                        return;
                    }
                    _Username = Encoder.GetString(LoginData, 1, 64).Trim();
                    string Verification = BitConverter.ToString(Hasher.ComputeHash(Encoder.GetBytes(CraftServer.Salt + _Username))).Replace("-", "").ToUpper();
                    if (Encoder.GetString(LoginData, 65, 32).ToUpper().Trim() == Verification)
                    {
                        if (CraftEvent.CraftPlayerJoinEvent(this) == true)
                            CraftServer.Broadcast(_Username + " connected to the server");
                        Console.WriteLine(_Username + " connected to the server from " + Address);
                        LoadData();
                        byte[] ServerIdentification = new byte[132];
                        ServerIdentification[0] = 0x00;
                        ServerIdentification[1] = 0x07;
                        Buffer.BlockCopy(StringToByte(CraftServer.Name), 0, ServerIdentification, 2, 64);
                        Buffer.BlockCopy(StringToByte(CraftServer.Motd), 0, ServerIdentification, 66, 64);
                        if (_Op == true)
                            ServerIdentification[131] = 0x64;
                        else
                            ServerIdentification[131] = 0x00;
                        _Client.GetStream().Write(ServerIdentification, 0, 131);
                        _ID = CraftServer.GetLevel(CraftServer.MainLevel).GetAvailableID();
                        Update();
                        CraftServer.GetLevel(CraftServer.MainLevel).ServeLevel(this);
                    }
                    else
                    {
                        KickPlayer("Failed to verify username!", Response.KickReason.BAD_LOGIN);
                        Console.WriteLine("Could not verify " + _Username + "'s username!");
                        return;
                    }
                }
                else
                {
                    KickPlayer("Must send login packet first!", Response.KickReason.BAD_PACKET_ID);
                    return;
                }
                while (_Disconnected == false)
                {
                    byte Identification = (byte)_Client.GetStream().ReadByte();
                    byte[] Data;
                    if (Identification == (byte)'G')
                    {
                        if (CraftServer.AllowWOM == false)
                        {
                            KickPlayer("No World of Minecraft clients!", Response.KickReason.WOM_FORBIDDEN);
                            Console.WriteLine(_Username + " was using WOM when it is disabled in server.properties!");
                            return;
                        }
                    }
                    else if (Identification == 0x05) //set block
                    {
                        Data = new byte[8];
                        _Client.GetStream().Read(Data, 0, 8);
                        if (Data[7] > (byte)49)
                        {
                            KickPlayer("Invalid block ID!", Response.KickReason.BAD_PACKET_ID);
                            Console.WriteLine(_Username + " tried to place an invalid block ID!");
                            return;
                        }
                        Point3D ReqChangePos = new Point3D(Convert.ToDouble(ByteToShort(Data, 0)), Convert.ToDouble(ByteToShort(Data, 2)), Convert.ToDouble(ByteToShort(Data, 4)));
                        if (Data[6] == 0x01) //building
                        {
                            if (ReqChangePos.Y >= CraftServer.GetLevel(_LevelIn).Height - 1)
                                return;
                            if (CraftEvent.CraftPlayerBuildEvent() == false)
                            {
                                CraftServer.GetLevel(_LevelIn).SetBlock((int)ReqChangePos.X, (int)ReqChangePos.Y, (int)ReqChangePos.Z, Data[7]);
                            }
                            else
                            {
                                SendBlockChange(ReqChangePos, BlockType.Air);
                            }
                        }
                        else if (Data[6] == 0x00) //breaking
                        {
                            if (ReqChangePos.Y >= CraftServer.GetLevel(_LevelIn).Height - 1)
                                return;
                            if (CraftEvent.CraftPlayerBreakEvent() == false)
                            {
                                CraftServer.GetLevel(_LevelIn).SetBlock((int)ReqChangePos.X, (int)ReqChangePos.Y, (int)ReqChangePos.Z, BlockType.Air);
                            }
                            else
                            {
                                SendBlockChange(ReqChangePos, CraftServer.GetLevel(_LevelIn).GetBlock((int)Math.Floor(ReqChangePos.X), (int)Math.Floor(ReqChangePos.Y), (int)Math.Floor(ReqChangePos.Z)));
                            }
                        }
                        else
                        {
                            KickPlayer("Unhandled build constructor", Response.KickReason.BAD_PACKET_ID);
                            return;
                        }
                    }
                    else if (Identification == 0x08) //position / orientation
                    {
                        Data = new byte[8];
                        _Client.GetStream().ReadByte();
                        _Client.GetStream().Read(Data, 0, 8);
                        ushort x = ByteToShort(Data, 0);
                        ushort y = ByteToShort(Data, 2);
                        ushort z = ByteToShort(Data, 4);
                        Point3D RequestedLocation = new Point3D(Convert.ToDouble(x / 32), Convert.ToDouble(y / 32), Convert.ToDouble(z / 32));
                        byte RequestedPitch = Data[7];
                        byte RequestedYaw = Data[6];
                        if (CraftEvent.CraftPlayerMoveEvent(this, RequestedLocation, RequestedPitch, RequestedYaw) == false)
                        {
                            _Pitch = RequestedPitch;
                            _Yaw = RequestedYaw;
                            _Position = RequestedLocation;
                            GlobalPositionUpdate(_Position);
                            Update();
                        }
                        else
                        {
                            byte[] SetBackPacket = new byte[10];
                            SetBackPacket[0] = 0x08;
                            unchecked { SetBackPacket[1] = (byte)-1; }
                            ShortToByte((short)_Position.X).CopyTo(SetBackPacket, 2);
                            ShortToByte((short)_Position.Y).CopyTo(SetBackPacket, 4);
                            ShortToByte((short)_Position.Z).CopyTo(SetBackPacket, 6);
                            SetBackPacket[8] = _Yaw;
                            SetBackPacket[9] = _Pitch;
                            try { _Client.GetStream().Write(SetBackPacket, 0, 10); }
                            catch { Disconnect(); return; }
                        }
                    }
                    else if (Identification == 0x0d) //message
                    {
                        Data = new byte[64];
                        _Client.GetStream().ReadByte();
                        _Client.GetStream().Read(Data, 0, 64);
                        string ChatMessage = Encoder.GetString(Data).Trim();
                        if (ChatMessage.StartsWith("/"))
                        {
                            ChatMessage = ChatMessage.Replace("/", "");
                            if (CraftEvent.CraftPlayerCommandEvent(this, ChatMessage) == false) //handled = false
                            {
                                if (ChatMessage.ToLower() == "list")
                                {
                                    string Final = "&cPlayers Online: &f";
                                    foreach (CraftPlayer cp in CraftServer.CraftPlayers)
                                    {
                                        if (Final == "&cPlayers Online: &f")
                                            Final += cp.Username;
                                        else
                                            Final += ", " + cp.Username;
                                    }
                                    SendMessage(Final);
                                }
                                else if (ChatMessage.ToLower() == "op")
                                {
                                    SendMessage("&cThis command can only be used through the Console");
                                }
                                else if (ChatMessage.ToLower() == "crash")
                                {
                                    KickPlayer("Error: Server shutting down!");
                                    Console.WriteLine(_Username + " used /crash! XD");
                                    return;
                                }
                                else
                                    SendMessage("&cPowerBlock doesn't know that command!");
                            }
                        }
                        else
                        {
                            if (CraftEvent.CraftPlayerChatEvent(this, ChatMessage) == false)
                            {
                                if (_Op == true)
                                    CraftServer.Broadcast("<&c" + _Username + "&f> " + ChatMessage);
                                else
                                    CraftServer.Broadcast("<" + _Username + "> " + ChatMessage);
                                Console.WriteLine("<" + _Username + "> " + ChatMessage);
                            }
                        }
                    }
                    else
                    {
                        KickPlayer("Unexpected packet " + Identification.ToString());
                        return;
                    }
                    Update();
                }
            }
            catch
            {
                if (_Disconnected == false)
                {
                    Disconnect();
                }
            }
        }

        public void Teleport(Point3D TeleportPosition, byte Yaw, byte Pitch)
        {
            byte[] PlayerSpawn = new byte[10];
            PlayerSpawn[0] = 0x08;
            unchecked { PlayerSpawn[1] = (byte)-1; }
            ShortToByte((short)(TeleportPosition.X * 32)).CopyTo(PlayerSpawn, 2);
            ShortToByte((short)(TeleportPosition.Y * 32)).CopyTo(PlayerSpawn, 4);
            ShortToByte((short)(TeleportPosition.Z * 32)).CopyTo(PlayerSpawn, 6);
            PlayerSpawn[8] = Yaw;
            PlayerSpawn[9] = Pitch;
            _Client.GetStream().Write(PlayerSpawn, 0, 10);
            _Position = TeleportPosition;
            _Yaw = Yaw;
            _Pitch = Pitch;
            GlobalPositionUpdate(TeleportPosition);
            Update();
        }

        private void GlobalPositionUpdate(Point3D Position)
        {
            byte[] UpdatePacket = new byte[10];
            UpdatePacket[0] = 0x08;
            UpdatePacket[1] = _ID;
            Buffer.BlockCopy(ShortToByte((short)(Position.X * 32)), 0, UpdatePacket, 2, 2);
            Buffer.BlockCopy(ShortToByte((short)(Position.Y * 32)), 0, UpdatePacket, 4, 2);
            Buffer.BlockCopy(ShortToByte((short)(Position.Z* 32)), 0, UpdatePacket, 6, 2);
            UpdatePacket[8] = _Yaw;
            UpdatePacket[9] = _Pitch;
            foreach (CraftPlayer cp in CraftServer.CraftPlayers)
            {
                if (cp.LevelIn == _LevelIn && cp.Username != _Username)
                {
                    try { cp.Client.GetStream().Write(UpdatePacket, 0, 10); }
                    catch { Disconnect(); }
                }
            }
        }

        public void SendMap(string Name)
        {
            _ID = CraftServer.GetLevel(Name).GetAvailableID();
            CraftServer.GetLevel(Name).ServeLevel(this);
            _LevelIn = CraftServer.GetLevel(Name).Name;
            Update();
        }

        public void LoadData()
        {
            
        }

        public void SaveData()
        {
            
        }

        public void SendMessage(string Message)
        {
            try
            {
                byte[] Package = new byte[66];
                Package[0] = 0x0d;
                Package[1] = _ID;
                Buffer.BlockCopy(StringToByte(Message), 0, Package, 2, 64);
                _Client.GetStream().Write(Package, 0, 65);
            }
            catch (Exception MessageSendException)
            {
                Console.WriteLine("Error sending chat message to '" + _Username + "'\n" + MessageSendException.ToString());
                Disconnect();
            }
        }

        public void KickPlayer() { KickPlayer("You were kicked from the server!"); }
        public void KickPlayer(string Message) { KickPlayer(Message, Response.KickReason.UNSPECIFIED); }

        public void KickPlayer(string Message, Response.KickReason Reason)
        {
            if (CraftEvent.CraftPlayerKickEvent(this, Message, Reason) == false)
            {
                try
                {
                    byte[] Package = new byte[66];
                    Package[0] = 0x0e;
                    Package[1] = 1;
                    Buffer.BlockCopy(StringToByte(Message), 0, Package, 2, 64);
                    _Client.GetStream().Write(Package, 0, 66);
                    Console.WriteLine(_Username + " was kicked from the server!");
                    CraftServer.CleanupPlayer(_Username);
                    CraftServer.Broadcast(_Username + " was kicked from the server!");
                }
                catch
                {
                    Disconnect();
                }
            }
        }

        public void SendBlockChange(Point3D Pos, byte Bloc)
        {
            byte[] Package = new byte[8];
            Package[0] = 0x06;
            Buffer.BlockCopy(ShortToByte((short)Pos.X), 0, Package, 1, 2);
            Buffer.BlockCopy(ShortToByte((short)Pos.Y), 0, Package, 3, 2);
            Buffer.BlockCopy(ShortToByte((short)Pos.Z), 0, Package, 5, 2);
            Package[8] = Bloc;
            _Client.GetStream().Write(Package, 0, 8);
        }

        public ushort ByteToShort(byte[] x, int offset)
        {
            byte[] y = new byte[2];
            Buffer.BlockCopy(x, offset, y, 0, 2); Array.Reverse(y);
            return BitConverter.ToUInt16(y, 0);
        }

        public byte[] ShortToByte(short x)
        {
            byte[] y = BitConverter.GetBytes(x); Array.Reverse(y); return y;
        }

        public void Disconnect()
        {
            try
            {
                Console.WriteLine(_Username + " disconnected from the server.");
                _Disconnected = true;
                CraftEvent.CraftPlayerQuitEvent(this);
                CraftServer.CleanupPlayer(this);
            }
            catch { }
            byte[] DespawnPacket = new byte[2];
            DespawnPacket[0] = 0x0c;
            DespawnPacket[1] = _ID;
            foreach (CraftPlayer cp in CraftServer.CraftPlayers)
            {
                if (cp.LevelIn == _LevelIn)
                {
                    try { cp.Client.GetStream().Write(DespawnPacket, 0, 2); }
                    catch { cp.Disconnect(); }
                }
            }
            _Client.Client.Close();
            CraftEvent.CraftPlayerQuitEvent(this);
            _Disconnected = true;
        }

        public byte[] StringToByte(string Message)
        {
            byte[] Built = new byte[64];
            Built = Encoder.GetBytes(Message.PadRight(64).Substring(0, 64));
            return Built;
        }

        public bool Banned
        {
            get { return _Banned; }
            set
            {
                _Banned = value;
                SaveData();
            }
        }

        public bool Op
        {
            get { return _Op; }
        }

        public void SetOp(bool Value)
        {
            try
            {
                _Op = Value;
                byte[] Package = new byte[2];
                Package[0] = 0x0f;
                if (Value == true)
                    Package[1] = 0x64;
                else
                    Package[1] = 0x00;
                Client.GetStream().Write(Package, 0, 2);
                Update();
            }
            catch { KickPlayer("Could not write to output stream", Response.KickReason.INTERNAL_SERVER_ERROR); return; }
        }

        public void Update() { CraftServer.CleanupPlayer(_Username); CraftServer.AddPlayer(this); }

        public byte ID { get { return _ID; } }
        public string Username { get { return _Username; } }
        public string Address { get { return _Client.Client.RemoteEndPoint.ToString(); } }
        public string LevelIn { get { return _LevelIn; } }
        public bool Disconnected { get { return _Disconnected; } }
        public byte Pitch { get { return _Pitch; } }
        public byte Yaw { get { return _Yaw; } }
        public Point3D Position { get { return _Position; } }
        public TcpClient Client { get { return _Client; } }
    }
}
