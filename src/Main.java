import java.util.ArrayList;
import java.util.Scanner;

class Item {

  private String name, type;
  
  Item(String name, String type) {
    this.name = name;
    this.type = type;
  }

  public String toString() {
    if(this.type.equals("dir")) {
      return "D - " + this.name;
    }else {
      return "f - " + this.name;
    }
  }

  public String getName() {
    return this.name;
  }

  public void setName(String newname){
    this.name = newname;
  }

  public String getType() {
    return this.type;
  }

}

class Folder extends Item {

  private ArrayList<Item> childs;

  Folder(String name) {
    super(name, "dir");
    this.childs = new ArrayList<>();
  }

  public String getContent() throws Exception {
    throw new Exception("Erro: Isto é um diretório");
  }

  public ArrayList<Item> getChilds() {
    return this.childs;
  }

  public int countChilds() {
    return this.childs.size();
  }

  public void removeDir(String name) throws Exception {
    System.out.println("removendo name: "+name);
    for (Item item : this.childs) {
      if (item.getName().equals(name) && item.getType().equals("dir")) {
        if(((Folder)item).countChilds() == 0) {
          System.out.println("item: "+item);
          this.childs.remove(item);
          System.out.println("this.childs: "+this.childs);
          return;
        } else {
          throw new Exception("Erro: Diretório não está vazio");
        }
      }
    }
    throw new Exception("Erro: Diretório não existe");
  }

  public Item getChild(String name) {
    for (Item item : this.childs) {
      if (item.getName().equals(name)) {
        return item;
      }
    }
    return null;
  }

  public void addFile(File file) throws Exception {
    for (Item item : this.childs) {
      if (item.getName().equals(file.getName())) {
        throw new Exception("Erro: Já existe um File ou diretório com este nome");
      }
    }
    this.childs.add(file);
  }

  public void removeFile(String name) throws Exception {
    for (Item item : this.childs) {
      if (item.getName().equals(name) && item.getType().equals("file")) {
        this.childs.remove(item);
        return;
      }
    }
    throw new Exception("Erro: Arquivo não existe");
  }

  public void addDir(Folder dir) throws Exception {
    for (Item item : this.childs) {
      if (item.getName().equals(dir.getName())) {
        throw new Exception("Erro: Já existe um File ou diretório com este nome");
      }
    }
    this.childs.add(dir);
  }

  public File detailFile(String name) throws Exception {
    for (Item item : this.childs) {
      if (item.getName().equals(name) && item.getType().equals("file")) {
        return (File) item;
      }
    }
    throw new Exception("Erro: Este arquivo é um diretório ou não existe");
  }

  public void tree(String prefix) {
    System.out.println(prefix + this.toString());
    for (Item item : this.childs) {
      if (item.getType().equals("dir")) {
        ((Folder) item).tree(prefix + "  ");
      } else {
        System.out.println(prefix + "  " + item.toString());
      }
    }
  }
}

class File extends Item {

  private String content;

  File(String name, String content) {
    super(name, "file");
    this.content = content;
  }

  public String getContent() {
    return this.content;
  }
  public void setContent(String newcontent) {
    this.content = newcontent;
  }
  public void appendContent(String morecontent) {
    this.content += morecontent;
  }
}

class State {
  ArrayList<String> history;
  ArrayList<String> currentPath;
  
  State(){
    history = new ArrayList<>();
    currentPath = new ArrayList<>();
  }

  void addToPath(String path) {
    currentPath.add(path);
  }

  void removeFromPath() throws Exception {
    if(currentPath.size() > 1){
      currentPath.remove(currentPath.size() - 1);
    }else{
      throw new Exception("Erro: Já está na raiz");
    }
  }

  String getCurrentPath() {
    String path = "";
    for(String p : currentPath) {
      path += "/" + p;
    }
    return path;
  }

  void addToHistory(String command) {
    history.add(command); 
  }
}

public class Main {

  static Folder mkdir(Folder current, String name) throws Exception {
    Folder toAdd = new Folder(name);
    current.addDir(toAdd);
    return current;
  }

  static Folder rmdir(Folder current, String name) throws Exception {
    current.removeDir(name);
    return current;
  }

  static void tree(Folder current) {
    current.tree("");
  }

  static void rename(Item i, String newname) {
    i.setName(newname);
  }

  static void ls(Folder Folder) {
    ArrayList<Item> lista = Folder.getChilds();

    ArrayList<Folder> Folders = new ArrayList<>();
    ArrayList<File> Files = new ArrayList<>();

    for(Item i : lista) {
      if(i.getType().equals("dir")) {
        Folders.add((Folder) i);
      }else {
        Files.add((File) i);
      }
    }

    for(Folder i : Folders) {
      System.out.println(i);
    }

    for(File i : Files) {
      System.out.println(i);
    }
  }

  static void cat(File file){
    System.out.println(file.getContent());
  }

  static File touch(String name, String content){
    File f = new File(name, content);
    return f;
  }



  public static void main(String[] args) {
    Folder currentDir = new Folder("/");
    State state = new State();
    state.addToPath(currentDir.getName());


    Scanner s = new Scanner(System.in);

    String comando = "";

    do{
      
      System.out.print("✗ "+currentDir.getName()+" ➜ ");
      comando = s.nextLine();

      comando = comando.trim();
      String[] partes = comando.split(" ");

      try{
        switch(partes[0]){
          case "mkdir":
            currentDir = mkdir(currentDir, partes[1]);
            break;
          case "rmdir":
            currentDir = rmdir(currentDir, partes[1]);
            break;
          case "tree":
            tree(currentDir);
            break;
          case "rename":
            Item i = currentDir.getChild(partes[1]);
            rename(i, partes[2]);
            break;
          case "ls":
            ls(currentDir);
            break;
          case "touch":
            if(partes.length == 2) {
              File f = touch(partes[1], "");
              currentDir.addFile(f);
              break;
            }
            File f = touch(partes[1], partes[2]);
            currentDir.addFile(f);
            break;
          case "echo":
            int nPartes = partes.length;

            String conteudo = "";
            for(int i2 = 1; i2 < nPartes -2; i2++) {
              conteudo += partes[i2] + " ";
            }
            
            boolean substituir = partes[nPartes -2].equals(">");
            String nomeArquivo = partes[nPartes -1];
            Item item = currentDir.getChild(nomeArquivo);
            if(item == null) {
              item = new File(nomeArquivo, "");
            }
            if(item.getType().equals("dir")) {
              throw new Exception("Erro: Não é possível escrever em um diretório");
            }

            try {
              if(substituir) {
                ((File) item).setContent(conteudo.trim());
              } else {
                ((File) item).appendContent(conteudo.trim());
              }
            } catch (Exception e) {
              item = new File(nomeArquivo, conteudo.trim());
              currentDir.addFile((File) item);
            }
            break;
          case "cat":
            File file = currentDir.detailFile(partes[1]);
            cat(file);
            break;
          case "rm":
            currentDir.removeFile(partes[1]);
            break;
          case "exit":
            System.out.println("Saindo...");
            break;
          default:
            System.out.println("Erro: Comando não reconhecido");
        }
      }catch(Exception e){
        System.out.println(e.getMessage());
      }

    }while(!comando.equals("exit"));


    s.close();

  }
}
