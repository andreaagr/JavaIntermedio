import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.io.InputStreamReader;
public class Cliente{
  private PrintStream ps;
  private String host;
  private int port;
  static Socket s;

  public void setHost(String host){
    this.host=host;
  }

  public void setPort(int port){
    this.port=port;
  }

  public void conectar() throws IOException{
    s=new Socket(host,port);
    ps=new PrintStream(s.getOutputStream());
  }

  public void enviar(String s) throws IOException{
    ps.println(s);
  }

  public void cerrar(){
    ps.close();
  }

  public static void main(String[] args) throws IOException{
    BufferedReader teclado=new BufferedReader(new InputStreamReader(System.in));
    Cliente cliente=new Cliente();
    System.out.println("Host: ");
    cliente.setHost(teclado.readLine());
    System.out.println("Puerto: ");
    cliente.setPort(Integer.parseInt(teclado.readLine()));
    cliente.conectar();
    String cadena="";
    Lectura lect = new Lectura(s);
    lect.start();
    while(true){
      System.out.println("Mensaje: ");
      cadena=teclado.readLine();
      if(cadena.equals("QUIT")){
        cliente.enviar(cadena);
        lect.setSalir();
        cliente.cerrar();//197
        break;
      }
      cliente.enviar(cadena);
    }


  }
}






class Lectura extends Thread{
String cadena = "";
Socket s;
boolean salir =false;
BufferedReader entrada;
Lectura(Socket s){
    this.s=s;
    try{
        entrada = new BufferedReader(new InputStreamReader(s.getInputStream()));
    }catch(IOException ioe){

    }

}

public void setSalir(){
  salir = true;
}


@Override
public void run(){
  try {
    do{
      cadena=recibir();
      System.out.println(cadena);
      cadena="";
      if(cadena.equals("null")){
        break;
      }
    }while (!salir);
  } catch(IOException ioe) {
    System.out.println("Terminada la conexion");
  }finally{
    try{
      cerrarFlujo();
    }catch(Exception e){
      System.out.println("Error al cerrar");
    }
  }
}


  public void cerrarFlujo() throws  IOException{
    entrada.close();
  }

public String recibir() throws IOException{
 	String str=entrada.readLine();
  return str;
}
}
