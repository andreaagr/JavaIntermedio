import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.PrintStream;
import java.util.ArrayList;


public class Servidor extends Thread{
  private String nombreCliente;
  private BufferedReader entrada;
  private PrintStream ps;
  Socket tmp;
  public static ArrayList<Datos>chat=new ArrayList<Datos>();

  public Servidor(Socket s) throws IOException{
    entrada=new BufferedReader(new InputStreamReader(s.getInputStream()));
    nombreCliente=s.getInetAddress().getCanonicalHostName();
    System.out.println("Conexion aceptada desde "+s.getRemoteSocketAddress());
    tmp=s;
  }
//_

  public String recibir() throws IOException{
    String str=entrada.readLine();


    if(str.equals("ADD")){
      add();
    }else if(str.equals("LIST")){
      lista();
    }else if(str.equals("QUIT")){
      quit();
    }
    else if(str.contains(" ")){
        System.out.println("Entró a comparar");
        String opciones[] = str.split(" ",2);
          if(opciones[0].equals("TEXT")){
            System.out.println("Entra a enviar TEXT");
            enviarTodos(opciones[1]);
          }

          else if(opciones[0].equals("TEXT_TO")){
            opciones = opciones[2].split(" ",2);
            enviarUno(opciones[1],opciones[0]);
         }
    }
    System.out.println("Se fue seguido");
    return str;
  }


  public void enviarTodos(String msg){
      int existe=buscarChat(nombreCliente);
      if(existe != -1){
          System.out.println("Entra a enviar a todos");
          for(Datos a : chat){
          try{
              Socket temp = a.getSocket();
              System.out.println("Hola");
              PrintStream ps = new PrintStream(temp.getOutputStream());

              ps.println(nombreCliente+" dijo "+msg);
          }catch(Exception e){
              System.out.println("Hubo un error");
          }
        }
      }else{
            System.out.println("No estas en el chat, teclea ADD");
      }
  }
  public void enviarUno(String msg, String usuario){
    int existe=buscarChat(nombreCliente);
    if(existe != -1){
        for(Datos a: chat){
          if(a.getUser().equals(usuario)){
            try{
              PrintStream ps = new PrintStream(a.getSocket().getOutputStream());
            }catch(Exception e){
              System.out.println("Hubo un error");
            }
            ps.println(nombreCliente+" te envió por privado: "+msg);
          }
        }
    }else
        System.out.println("No estas en el chat, teclea ADD");
  }
//____________________________________
  //_____________________________________________________________
   public int buscarChat(String user){
        Datos t;
        for(int i=0;i<chat.size();i++){
                t=chat.get(i);
                if((t.getUser()).equals(user))
                        return i;
        }
        return -1;
    }

    public void lista(){
        int existe;
        existe=buscarChat(nombreCliente);
        if(existe != -1){
              System.out.println("Personas conectadas: ");
              for(int x=0;x<chat.size();x++){
                  System.out.println(chat.get(x).getUser());
              }
        }else{
          System.out.println("No estas en el chat :(");
        }
    }

    public void add(){
        Datos d=new Datos(nombreCliente,tmp);
        int existe=buscarChat(nombreCliente);
        if(existe== -1){
                System.out.println("Bienvenido al chat (:");
                chat.add(d);
        }
        else
                System.out.println("Este usuario ya esta registrado, elige otro nombre para evitar confusiones :(");
    }

    public void quit(){
        int existe;
        Datos tmp;
        //BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
        //String usuario;
        //existe=buscarChat(tmp.getUser());
        //if(existe != -1){
        //        System.out.println("¿A quién deseas sacar del chat?");
        //        usuario=teclado.readLine();
        //        existe=buscarChat(usuario);
        //        if(existe != -1){
        existe=buscarChat(nombreCliente);
        if(existe != -1){
                        tmp=chat.get(existe);
                        tmp.cerrarFlujo();
                        chat.remove(existe);
        }else{
                        System.out.println("No estas en el chat aún");
        }
        //        }
        //        else
        //                System.ot.println("El usuario indicado no esta en el chat :(");
        //}
        //else
        //        System.out.println("No estas en el chat D:");
    }
  //_____________________________________________________________

  public void cerrarFlujo() throws  IOException{
    entrada.close();
  }

  @Override
  public void run(){
    try {
      BufferedReader teclado=new BufferedReader(new InputStreamReader(System.in));
      String cadena="";
      do{
        cadena=recibir();
        /*
        String opciones[] = cadena.split(" ");
        for(String i : opciones
        {

        })
        System.out.println(nombreCliente+" dice: "+cadena);
        */
      }while (!cadena.equals("QUIT"));
    } catch(IOException ioe) {
      System.out.println("Se cerro la conexion con "+nombreCliente);
    }finally{
      try{
        cerrarFlujo();
      }catch(Exception e){
        System.out.println("Error al cerrar");
      }
    }
  }

  public static void main(String[] args) throws IOException{
    ServerSocket ss;
    ss=new ServerSocket(0);
    System.out.println("Servidor aceptando conexiones en el puerto "+ss.getLocalPort());
    while(true){
      Socket cliente =ss.accept();
      Servidor hilo=new Servidor(cliente);
      hilo.start();
    }
  }
}

class Datos{
    private String usuario;
    private Socket s;
    private String nombre;
    Datos(String u,Socket s){
      usuario=u;
      this.s=s;
    }

    String getUser(){
        return usuario;
    }

    Socket getSocket(){
      return s;
    }
    void cerrarFlujo(){
      try{
            s.close();
      }catch(Exception e){}

    }

}
