import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

class Item {

  private String name, type;
  private Date creationDate;
  private Date modificationDate;
  
  Item(String name, String type) {
    this.name = name;
    this.type = type;
    this.creationDate = new Date();
    this.modificationDate = new Date();
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
    this.modificationDate = new Date();
  }

  public String getType() {
    return this.type;
  }
  
  public Date getCreationDate() {
    return this.creationDate;
  }
  
  public Date getModificationDate() {
    return this.modificationDate;
  }
  
  public void updateModificationDate() {
    this.modificationDate = new Date();
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
  
  public long getSize() {
    long size = 0;
    for (Item item : this.childs) {
      if (item.getType().equals("file")) {
        size += ((File) item).getContentLength();
      } else {
        size += ((Folder) item).getSize();
      }
    }
    return size;
  }
  
  public void findByName(String name, String currentPath, ArrayList<String> results) {
    String itemPath = currentPath.equals("/") ? "/" + this.getName() : currentPath + "/" + this.getName();
    if (this.getName().contains(name)) {
      results.add(itemPath);
    }
    for (Item item : this.childs) {
      if (item.getType().equals("dir")) {
        ((Folder) item).findByName(name, itemPath, results);
      } else {
        String filePath = itemPath + "/" + item.getName();
        if (item.getName().contains(name)) {
          results.add(filePath);
        }
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
    this.content = newcontent + "\n";
    this.updateModificationDate();
  }
  public void appendContent(String morecontent) {
    this.content += morecontent + "\n";
    this.updateModificationDate();
  }

  public int getContentLength() {
    return this.content.length();
  }
  public int getContentLines() {
    return this.content.split("\n").length;
  }
  public int getContentWords() {
    return this.content.replace("\n", " ").split(" ").length;
  }
  public int getContentChars() {
    return this.content
      .replace(" ", "")
      .replace("\n", "")
      .length();
  }

}

class State {
  ArrayList<String> history;
  ArrayList<String> currentPath;
  ArrayList<Folder> directoryStack;
  Folder rootDir;
  
  State(Folder root){
    history = new ArrayList<>();
    currentPath = new ArrayList<>();
    directoryStack = new ArrayList<>();
    rootDir = root;
    directoryStack.add(root);
  }

  void addToPath(String path) {
    currentPath.add(path);
  }

  void removeFromPath() throws Exception {
    if(currentPath.size() > 1){
      currentPath.remove(currentPath.size() - 1);
      directoryStack.remove(directoryStack.size() - 1);
    }else{
      throw new Exception("Erro: Já está na raiz");
    }
  }
  
  void addToDirectoryStack(Folder dir) {
    directoryStack.add(dir);
  }
  
  Folder getCurrentDir() {
    return directoryStack.get(directoryStack.size() - 1);
  }
  
  void resetToRoot() {
    currentPath.clear();
    directoryStack.clear();
    currentPath.add("/");
    directoryStack.add(rootDir);
  }

  String getCurrentPath() {
    if(currentPath.size() == 1 && currentPath.get(0).equals("/")) {
      return "/";
    }
    String path = "";
    for(String p : currentPath) {
      if(!p.equals("/")) {
        path += "/" + p;
      }
    }
    return path.isEmpty() ? "/" : path;
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

  static void lsDetailed(Folder folder) {
    ArrayList<Item> lista = folder.getChilds();
    
    ArrayList<Folder> Folders = new ArrayList<>();
    ArrayList<File> Files = new ArrayList<>();

    for(Item i : lista) {
      if(i.getType().equals("dir")) {
        Folders.add((Folder) i);
      }else {
        Files.add((File) i);
      }
    }

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    
    System.out.println(String.format("%-4s %-20s %-10s %-20s %-20s", 
                                      "Tipo", "Nome", "Tamanho", "Criação", "Modificação"));
    System.out.println("─".repeat(80));
    
    for(Folder dir : Folders) {
      String tipo = "DIR ";
      String nome = dir.getName();
      String tamanho = dir.getSize() + "b";
      String criacao = sdf.format(dir.getCreationDate());
      String modificacao = sdf.format(dir.getModificationDate());
      
      System.out.println(String.format("%-4s %-20s %-10s %-20s %-20s", 
                                        tipo, nome, tamanho, criacao, modificacao));
    }

    for(File file : Files) {
      String tipo = "FILE";
      String nome = file.getName();
      String tamanho = file.getContentLength() + "b";
      String criacao = sdf.format(file.getCreationDate());
      String modificacao = sdf.format(file.getModificationDate());
      
      System.out.println(String.format("%-4s %-20s %-10s %-20s %-20s", 
                                        tipo, nome, tamanho, criacao, modificacao));
    }
  }

  static void cat(File file){
    System.out.println(file.getContent());
  }

  static File touch(String name, String content){
    File f = new File(name, content);
    return f;
  }

  static void echo(Folder currentDir, String conteudo, boolean substituir, String nomeArquivo) throws Exception {
    conteudo = conteudo.replace("\\n", "\n");
    
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
  }

  static void rm(Folder currentDir, String name) throws Exception {
    currentDir.removeFile(name);
  }

  static void head(Folder currentDir, String nomeArquivo, int nLinhas) throws Exception {
    File fileHead = currentDir.detailFile(nomeArquivo);
    String[] linhas = fileHead.getContent().split("\n");
    nLinhas = Math.min(nLinhas, linhas.length);
    for(int i = 0; i < nLinhas; i++) {
      System.out.println(linhas[i]);
    }
  }

  static void tail(Folder currentDir, String nomeArquivo, int nLinhas) throws Exception {
    File fileHead = currentDir.detailFile(nomeArquivo);
    String[] linhas = fileHead.getContent().split("\n");
    String[] linhasRevertidas = new String[linhas.length];
    for(int i = 0; i < linhas.length; i++) {
      linhasRevertidas[i] = linhas[linhas.length - 1 - i];
    }
    linhas = linhasRevertidas;
    nLinhas = Math.min(nLinhas, linhas.length);
    for(int i = nLinhas-1; i >= 0; i--) {
      System.out.println(linhas[i]);
    }
  }

  static void wc(Folder currentDir, String nomeArquivo) throws Exception {
    File fileWc = currentDir.detailFile(nomeArquivo);
    System.out.println("Linhas: " + fileWc.getContentLines());
    System.out.println("Palavras: " + fileWc.getContentWords());
    System.out.println("Caracteres: " + fileWc.getContentChars());
  }

  static void cd(State state, String destino) throws Exception {
    if(destino.equals("..")) {
      state.removeFromPath();
    } else if(destino.equals("/")) {
      state.resetToRoot();
    } else {
      Folder currentDir = state.getCurrentDir();
      Item item = currentDir.getChild(destino);
      if(item == null) {
        throw new Exception("Erro: Diretório não existe");
      }
      if(!item.getType().equals("dir")) {
        throw new Exception("Erro: Não é um diretório");
      }
      state.addToPath(destino);
      state.addToDirectoryStack((Folder) item);
    }
  }

  static void pwd(State state) {
    System.out.println(state.getCurrentPath());
  }

  static void find(Folder startDir, String name, String startPath) {
    ArrayList<String> results = new ArrayList<>();
    startDir.findByName(name, startPath.equals("/") ? "" : startPath, results);
    if(results.isEmpty()) {
      System.out.println("Nenhum resultado encontrado");
    } else {
      for(String result : results) {
        System.out.println(result);
      }
    }
  }

  static void grep(Folder currentDir, String termo, String nomeArquivo) throws Exception {
    File file = currentDir.detailFile(nomeArquivo);
    String[] linhas = file.getContent().split("\n");
    boolean encontrou = false;
    for(int i = 0; i < linhas.length; i++) {
      if(linhas[i].contains(termo)) {
        System.out.println((i+1) + ": " + linhas[i]);
        encontrou = true;
      }
    }
    if(!encontrou) {
      System.out.println("Termo não encontrado no arquivo");
    }
  }

  static void stat(Folder currentDir, String nome) throws Exception {
    Item item = currentDir.getChild(nome);
    if(item == null) {
      throw new Exception("Erro: Item não existe");
    }
    
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    System.out.println("Nome: " + item.getName());
    System.out.println("Tipo: " + (item.getType().equals("dir") ? "Diretório" : "Arquivo"));
    System.out.println("Data de criação: " + sdf.format(item.getCreationDate()));
    System.out.println("Última modificação: " + sdf.format(item.getModificationDate()));
    
    if(item.getType().equals("file")) {
      File file = (File) item;
      System.out.println("Tamanho: " + file.getContentLength() + " bytes");
      System.out.println("Linhas: " + file.getContentLines());
      System.out.println("Palavras: " + file.getContentWords());
    } else {
      Folder folder = (Folder) item;
      System.out.println("Número de itens: " + folder.countChilds());
      System.out.println("Tamanho total: " + folder.getSize() + " bytes");
    }
  }

  static void du(Folder currentDir, String nomeDiretorio) throws Exception {
    Item item = currentDir.getChild(nomeDiretorio);
    if(item == null) {
      throw new Exception("Erro: Diretório não existe");
    }
    if(!item.getType().equals("dir")) {
      throw new Exception("Erro: Não é um diretório");
    }
    Folder folder = (Folder) item;
    long size = folder.getSize();
    System.out.println(size + " bytes\t" + nomeDiretorio);
  }

  static void history(State state) {
    ArrayList<String> hist = state.history;
    if(hist.isEmpty()) {
      System.out.println("Histórico vazio");
    } else {
      for(int i = 0; i < hist.size(); i++) {
        System.out.println((i+1) + "  " + hist.get(i));
      }
    }
  }

  public static void main(String[] args) {
    Folder rootDir = new Folder("/");
    State state = new State(rootDir);
    state.addToPath("/");


    Scanner s = new Scanner(System.in);

    String comando = "";

    do{
      Folder currentDir = state.getCurrentDir();
      
      System.out.print("✗ "+state.getCurrentPath()+" ➜ ");
      comando = s.nextLine();

      comando = comando.trim();
      String[] partes = comando.split(" ");
      
      if(!comando.isEmpty() && !comando.equals("exit")) {
        state.addToHistory(comando);
      }

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
            if(partes.length > 1 && partes[1].equals("-l")) {
              lsDetailed(currentDir);
            } else {
              ls(currentDir);
            }
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
            echo(currentDir, conteudo, substituir, nomeArquivo);
            break;
          case "cat":
            File file = currentDir.detailFile(partes[1]);
            cat(file);
            break;
          case "rm":
            rm(currentDir, partes[1]);
            break;
          case "head":
            int nLinhas = partes.length > 2 ? Integer.parseInt(partes[2]) : 10;
            head(currentDir, partes[1], nLinhas);
            break;
          case "tail":
            int nLinhasTail = partes.length > 2 ? Integer.parseInt(partes[2]) : 10;
            tail(currentDir, partes[1], nLinhasTail);
            break;
          case "wc":
            wc(currentDir, partes[1]);
            break;
          case "cd":
            if(partes.length == 1) {
              state.resetToRoot();
            } else {
              cd(state, partes[1]);
            }
            break;
          case "pwd":
            pwd(state);
            break;
          case "find":
            if(partes.length >= 4 && partes[2].equals("-name")) {
              Folder searchDir = state.getCurrentDir();
              if(!partes[1].equals(".")) {
                Item item = searchDir.getChild(partes[1]);
                if(item != null && item.getType().equals("dir")) {
                  searchDir = (Folder) item;
                } else {
                  throw new Exception("Erro: Diretório não encontrado");
                }
              }
              find(searchDir, partes[3], state.getCurrentPath() + (partes[1].equals(".") ? "" : "/" + partes[1]));
            } else {
              throw new Exception("Uso: find <diretorio> -name <nome>");
            }
            break;
          case "grep":
            if(partes.length >= 3) {
              grep(currentDir, partes[1], partes[2]);
            } else {
              throw new Exception("Uso: grep <termo> <arquivo>");
            }
            break;
          case "stat":
            stat(currentDir, partes[1]);
            break;
          case "du":
            du(currentDir, partes[1]);
            break;
          case "history":
            history(state);
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
