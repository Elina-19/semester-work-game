package ru.itis.server;

import ru.itis.exceptions.ServerAlreadyStartException;
import ru.itis.general.entities.Car;
import ru.itis.general.entities.Player;
import ru.itis.general.entities.Room;
import ru.itis.general.helpers.PlayerParser;
import ru.itis.protocol.Constants;
import ru.itis.protocol.Message;
import ru.itis.listeners.IServerEventListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.ServerException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Server implements IServer{
    protected int port;
    protected boolean started;

    protected List<IServerEventListener> listeners;

    protected ServerSocket server;
    protected static List<Connection> connections;

    protected List<Room> rooms;
    protected List<Car> availableCars;

    public Server(int port){
        this.port = port;
        started = false;
        listeners = new ArrayList<>();
        connections = new ArrayList<>();
    }

    @Override
    public void start() throws ServerException {
        rooms = new ArrayList<>();
        initAvailableCars();

        try{
            server = new ServerSocket(this.port);
            started = true;

            while(true){
                Socket s = server.accept();
                handleConnection(s);
            }
        }
        catch(IOException ex){
            throw new ServerException("Problem with server starting.", ex);
        }
    }

    protected void handleConnection(Socket socket) throws ServerException{
        try {
            Connection connection = new Connection(this, socket);
            this.connections.add(connection);

            new Thread(connection).start();
        }
        catch(IOException ex){
            throw new ServerException("Problem with handling connection.", ex);
        }
    }

    protected void initAvailableCars(){
        availableCars = new ArrayList<>();

        availableCars.add(new Car("red", 10));
        availableCars.add(new Car("green", 15));
        availableCars.add(new Car("blue", 30));
        availableCars.add(new Car("yellow", 20));
        availableCars.add(new Car("black", 50));
    }

    @Override
    public void registerListener(IServerEventListener listener){
        if (started){
            throw new ServerAlreadyStartException("Server has been started already. Can't register listener.");
        }

        listener.init(this);
        this.listeners.add(listener);
    }

    @Override
    public void registerListener(List<IServerEventListener> listeners) {
        if (started){
            throw new ServerAlreadyStartException("Server has been started already. Can't register listener.");
        }

        for(IServerEventListener listener: listeners){
            listener.init(this);
        }
        this.listeners.addAll(listeners);
    }

    @Override
    public void sendMessage(Connection connection, Message message){
        try {
            connection.getOutputStream().writeMessage(message);
        }catch (IOException e){
            removeConnection(connection);
        }
    }

    @Override
    public void sendMulticastMessage(Room room, Message message){
        List<Player> players = room.getPlayers();

        for (Connection connection: connections){
            if (players.contains(connection.getPlayer())){
                sendMessage(connection, message);
            }
        }
    }

    @Override
    public Connection getConnectionById(Integer id){
        for (Connection connection: connections){
            if (connection.getId() == id){
                return connection;
            }
        }

        return null;
    }

    @Override
    public List<Connection> getAllConnections(){
        return connections;
    }

    @Override
    public void removeConnection(Connection connection) {
        Iterator<Connection> iterator = connections.iterator();

        while (iterator.hasNext()){
            Connection conn = iterator.next();

            if (connection.getId() == conn.getId()){
                handleRemovePlayer(connection.getPlayer());
                iterator.remove();
            }
        }
    }

    protected void handleRemovePlayer(Player player){
        PlayerParser parser = new PlayerParser();

        if (player.inRoom()){
            Room room = player.getRoom();
            player.exitRoom();

            Message message =  new Message(Constants.SUCCESS_EXIT_ROOM,
                    parser.serializeObject(room.getPlayers()));

            sendMulticastMessage(room, message);
        }
    }

    @Override
    public List<Room> getAllRooms() {
        return rooms;
    }

    @Override
    public Room createRoom() {
        Room room = new Room();
        rooms.add(room);

        return room;
    }

    @Override
    public void removeRoom(Room room) {
        rooms.remove(room);
    }

    @Override
    public List<IServerEventListener> getListeners() {
        return listeners;
    }

    @Override
    public List<Car> getAvailableCars() {
        return availableCars;
    }
}
