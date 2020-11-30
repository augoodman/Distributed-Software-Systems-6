import javax.swing.*;
import java.io.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.util.Objects;
import javax.sound.sampled.*; //api classes to play wav file

/**

 * Purpose: demonstrate the use of a thread to provide interruptable background
 * playing of a wav file. To make this example work, there must be a wav
 * file in the project directory whose name matches the user-selected node
 * in the JTree. Select the tree node (example ComeMonday) then select
 * the Music-->Play menu item to play the file: ComeMonday.wav in the
 * project directory. Notice you can select a new node and then play to
 * move to a new song. Or, select Play again to restart from the beginning
 * of the current song.
 * You can generate a wav file for your an mp3 using the web site:
 *      http://audio.online-convert.com/convert-to-wav
 *
 * <p>
 * Ser321 Principles of Distributed Software Systems
 * @author Tim Lindquist (Tim.Lindquist@asu.edu) CIDSE - Software Engineering
 *                       Ira Fulton Schools of Engineering, ASU Polytechnic
 * @version August, 2020
 */
public class MusicThread extends MusicLibraryGui implements
                                                 TreeWillExpandListener,
                                                 ActionListener,
                                                 TreeSelectionListener {
   private PlayWavThread player = null;
   private boolean stopPlaying;
   private static String userName = null;

   public MusicThread(String base) {
      super(base);
      stopPlaying = false;
      for (JMenuItem[] userMenuItem : userMenuItems) {
         for (JMenuItem jMenuItem : userMenuItem) {
            jMenuItem.addActionListener(this);
         }
      }
      tree.addTreeSelectionListener(this);
      tree.addTreeWillExpandListener(this);
      setVisible(true);
   }

   /**
    * a method to be called by music playing threads to determine
    * whether they should stop
    **/
   public boolean sezToStop(){
      return stopPlaying;
   }

   /**
    * create and initialize nodes in the JTree of the left pane.
    * buildInitialTree is called by MusicLibraryGui to initialize the JTree.
    * Classes that extend MusicLibraryGui should override this method to 
    * perform initialization actions specific to the extended class.
    * The default functionality is to set base as the label of root.
    * In your solution, you will probably want to initialize by deserializing
    * your library and building the tree.
    * @param root Is the root node of the tree to be initialized.
    * @param base Is the string that is the root node of the tree.
    */
   public void buildInitialTree(DefaultMutableTreeNode root, String base){
      try{
         System.out.println("buildInitialTree called by Gui constructor");
         // put some sample nodes in the tree so the user doesn't have
         // to select restore.
         initializeTree();
      }catch (Exception ex){
         JOptionPane.showMessageDialog(this,"exception initial tree:"+ex);
         ex.printStackTrace();
      }
   }

   public void initializeTree( ){
      tree.removeTreeSelectionListener(this);
      tree.removeTreeWillExpandListener(this);
      try{
         DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
         DefaultMutableTreeNode root =
            (DefaultMutableTreeNode)model.getRoot();
         if(userName == null)
            userName = System.getProperty("user.name");
         System.out.println("user name is: "+ userName);
         //String sourceNames[] = {user,"Alone In Iz World","HanohanoCowboy",
         //                        "All The Greatest Hits","ComeMonday"};
	 File file = new File("./music");
	 File[] directories = file.listFiles((current, name) -> new File(current, name).isDirectory());
         root.setUserObject("music");
	 DefaultMutableTreeNode dirNode;
	 DefaultMutableTreeNode wavNode;
         assert directories != null;
         for (File directory : directories) {
            // create a node for this directory
            dirNode = new DefaultMutableTreeNode(directory.getName());
            model.insertNodeInto(dirNode, root, model.getChildCount(root));
            // within each directory find the .wav files
            File[] wavFiles = directory.listFiles((dir, name) -> name.endsWith(".wav"));
            assert wavFiles != null;
            for (File wavFile : wavFiles) {
               wavNode = new DefaultMutableTreeNode(wavFile.getName().replaceFirst("[.][^.]+$", ""));
               model.insertNodeInto(wavNode, dirNode, 0); //model.getChildCount(prevNode));
            }
         }
         // expand all the nodes in the JTree
         for(int r =0; r < tree.getRowCount(); r++){
            tree.expandRow(r);
         }
      }catch (Exception ex){
         JOptionPane.showMessageDialog(this,"exception initial tree:"+ex);
         ex.printStackTrace();
      }
      tree.addTreeSelectionListener(this);
      tree.addTreeWillExpandListener(this);
   }

   public void treeWillCollapse(TreeExpansionEvent tee) {
      tree.setSelectionPath(tee.getPath());
   }

   public void treeWillExpand(TreeExpansionEvent tee) {
      DefaultMutableTreeNode dmtn =
         (DefaultMutableTreeNode)tee.getPath().getLastPathComponent();
      System.out.println("will expand node: "+dmtn.getUserObject()+
                         " whose path is: "+tee.getPath());
   }

   public void valueChanged(TreeSelectionEvent e) {
      try{
         tree.removeTreeSelectionListener(this);
         DefaultMutableTreeNode node = (DefaultMutableTreeNode)
            tree.getLastSelectedPathComponent();
	 if (node != null) {
	     String nodeLabel = (String)node.getUserObject();
	     titleJTF.setText(nodeLabel);
	 }
      }catch (Exception ex){
         ex.printStackTrace();
      }
      tree.addTreeSelectionListener(this);
   }

   public void actionPerformed(ActionEvent e) {
      switch (e.getActionCommand()) {
         case "Exit":
            System.exit(0);
         case "Play":
            try {
               System.out.println("Play Selected");
               // get the currently selected node in the tree.
               // if the user hasn't already selected a node for which
               // there must be a wav file then exit ungracefully!
               if (player != null && player.isAlive()) {
                  System.out.println("Already playing: Interrupting the thread");
                  stopPlaying = true;
                  Thread.sleep(500); // give the thread time to complete
                  stopPlaying = false;
               }
               StringBuilder jTreeVarSelectedPath = new StringBuilder();
               Object[] paths = Objects.requireNonNull(tree.getSelectionPath()).getPath();
               for (int i = 0; i < paths.length; i++) {
                  jTreeVarSelectedPath.append(paths[i]);
                  if (i + 1 < paths.length) {
                     jTreeVarSelectedPath.append(File.separator);
                  }
               }
               player = new PlayWavThread(jTreeVarSelectedPath.toString(), this);
               player.start();
            } catch (InterruptedException ex) { // sleep may throw this exception
               System.out.println("MusicThread sleep was interrupted.");
               ex.printStackTrace();
            }
            break;
         case "Stop":
            try {
               if (player != null && player.isAlive()) {
                  System.out.println("Already playing: Interrupting the thread");
                  stopPlaying = true;
                  Thread.sleep(500); // give the thread time to complete
                  stopPlaying = false;
               }
            } catch (InterruptedException ex) { // sleep may throw this exception
               System.out.println("MusicThread stop.");
               ex.printStackTrace();
            }
            break;
         default:
            System.out.println(e.getActionCommand() + " Selected");
            break;
      }
   }

   public static void main(String[] args) {
      String topicName = args[0];
      System.out.println(topicName);
      userName = args[1];
      String password = args[2];
      try{
         new MusicThread(topicName);
      }catch (Exception ex){
         ex.printStackTrace();
      }

       // uncomment this line for verbose logging to the screen
       //BasicConfigurator.configure();
       if (args.length != 3) {
           System.out.println("Expected arguments: <topic-name(String)> <username(String)> <password(String)>");
           System.exit(1);
       }

       try {
           Publisher pub = new Publisher(topicName, "admin", "admin");
           BufferedReader commandLine = new BufferedReader(new InputStreamReader(System.in));

           // closes the connection and exit the system when 'exit' entered in the command line
           while (true) {
               System.out.println("Enter text to publish. Enter 'exit' to close the program.");
               String s = commandLine.readLine();
               if (s.equalsIgnoreCase("exit")) {
                   pub.connection.close();
                   System.exit(0);
               }
               if (pub.goPublish(s)) {
                   System.out.println("Published: " + s);
               } else {
                   System.out.println("Unable to publish: " + s);
               }
           }
       } catch (Exception e) {
           e.printStackTrace();
       }
   }
}

/**
 *  A thread class to play a wav file. PlayWavThread opens an audio input
 * stream and plays it. To allow play to be interrupted, each time a new
 * buffer of the wav file is read, the thread checks with the server to see
 * whether it should complete. The server signals by setting and returning
 * a boolean value indicating that playing the wav file should stop.
 **/
class PlayWavThread extends Thread {
   private final String aTitle;
   private final MusicThread parent;
   public PlayWavThread(String aTitle, MusicThread parent) {
      this.parent = parent;
      this.aTitle = aTitle;
   }

   public void run (){
      int BUFFER_SIZE = 4096;
      AudioInputStream audioStream;
      AudioFormat audioFormat;
      SourceDataLine sourceLine;
      try{
         Thread.sleep(100); //wait 200 milliseconds before playing the file.
         System.out.println("Playing the wav file: " +aTitle);
         //String fn = (aTitle.startsWith("Han")) ? aTitle+".mp3" : aTitle+".wav";
         String fn = aTitle+".wav";
         //audioStream = AudioSystem.getAudioInputStream(new File(aTitle+".wav"));
         audioStream = AudioSystem.getAudioInputStream(new File(fn));
         audioFormat = audioStream.getFormat();
         DataLine.Info i = new DataLine.Info(SourceDataLine.class, audioFormat);
         sourceLine = (SourceDataLine) AudioSystem.getLine(i);
         sourceLine.open(audioFormat);
         sourceLine.start();
         int nBytesRead = 0;
         byte[] abData = new byte[BUFFER_SIZE];
         while(nBytesRead != -1){
            try{
               if(parent.sezToStop()){
                  System.out.println("Interrupted playing: "+aTitle);
                  break;
               }
               nBytesRead = audioStream.read(abData, 0, abData.length);
               if (nBytesRead >= 0) {
                  @SuppressWarnings("unused")
                     int nBytesWritten = sourceLine.write(abData,0,nBytesRead);
               }
            } catch (Exception e){
               e.printStackTrace();
            }
         }
         sourceLine.drain();
         sourceLine.close();
         audioStream.close();
      }catch (Exception e){
         e.printStackTrace();
      }
   }
}
