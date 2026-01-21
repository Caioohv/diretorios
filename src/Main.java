package src;

import Java.util.scanner;

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
    this.childs = new ArrayList<Item>();
  }

  public String getContent() {
    throw new Exception("Erro: Isto é um diretório");
  }

  public String getChilds() {
    return this.childs;
  }

  public int countChilds() {
    return this.childs.size();
  }

  public void removeDir(String name) {
    for (Item item : this.childs) {
      if (item.getName().equals(name) && item.getType().equals("dir")) {
        if(item.countChilds() == 0) {
          this.childs.remove(item);
          return;
        } else {
          throw new Exception("Erro: Diretório não está vazio");
        }
      }
    }
    throw new Exception("Erro: Diretório não existe");
  }

  public String getChild(String name) {
    for (Item item : this.childs) {
      if (item.getName().equals(name)) {
        return item;
      }
    }
    return null;
  }

  public void addFile(File file) {
    for (Item item : this.childs) {
      if (item.getName().equals(file.getName())) {
        throw new Exception("Erro: Já existe um File ou diretório com este nome");
      }
    }
    this.childs.add(file);
  }

  public void addDir(Folder dir) {
    for (Item item : this.childs) {
      if (item.getName().equals(dir.getName())) {
        throw new Exception("Erro: Já existe um File ou diretório com este nome");
      }
    }
    this.childs.add(dir);
  }

  public File detailFile(String name) {
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
}

class State {
  ArrayList<String> history;
  ArrayList<String> currentPath;
  
  State(){
    history = new ArrayList<String>;
  }
}

public class Main {

  Folder mkdir(Folder current, String name){
    Folder toAdd = new Folder(name);
    current.addDir(toAdd);
  }

  Folder rmdir(Folder current, String name){
    current.removeDir(name);
  }

  void tree(Folder current) {
    current.tree("");
  }

  void rename(Item i, String newname) {
    i.setName(newname);
  }

  void ls(Folder Folder) {
    ArrayList<Item> lista = Folder.getChilds();

    ArrayList<Folder> Folders = new ArrayList<Folder>();
    ArrayList<File> Files = new ArrayList<Folder>();

    for(Item i : lista) {
      if(i.getType.equals("dir")) {
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

  void cat(File file){
    System.out.println(file.getContent());
  }

  File touch(String name, String content){
    File f = new File(name, content);
    return f;
  }



  public static void main(String[] args) {
    Folder currentDir = new Folder('/');


    Scanner s = new Scanner(System.in);

    String comando = "";

    do{
      
      System.out.print("✗ "+currentDir.getName()+" ➜ ");


    }while(!comando.equals("exit"));

  }
}
