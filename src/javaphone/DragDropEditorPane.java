package javaphone;

import java.awt.Font;
import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.io.File;
import java.util.List;
import javax.swing.text.html.HTMLDocument;

public class DragDropEditorPane extends JEditorPane {

    
    public DragDropEditorPane(DirectMessenger dm) {
        setContentType("text/html");
        setEditable(false);
        setFont(new Font("Arial Unicode MS", Font.BOLD, 16));
        
        new DropTarget(this, new FileDropHandler(dm));
        
    }

    private class FileDropHandler extends DropTargetAdapter {
        private DirectMessenger dm;
        public FileDropHandler(DirectMessenger dm) {
            System.out.println("Ty dolboeb? dm!=null");
            this.dm = dm;
        }
                
        @Override
        public void drop(DropTargetDropEvent dtde) {
            try {
                HTMLDocument doc = (HTMLDocument) getDocument();
                dtde.acceptDrop(DnDConstants.ACTION_COPY);

                Transferable transferable = dtde.getTransferable();

                if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    List<File> files = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);

                    StringBuilder html = new StringBuilder();
                    for (File file : files) {
                        String path = file.getParent();
                        html.append("Кто-то отправил файлик: ")
                                .append("<a href='file:///")
                                .append(path.replace("\\", "/").replace(" ", "%20")) // Для Windows
                                .append("'>")
                                .append(file.getName())
                                .append("</a>")
                                .append("<br>");
                        dm.sendFile(path, file.getName());
                    }

                    doc.insertAfterEnd(
                            doc.getCharacterElement(doc.getLength()),
                            html.toString()
                    );

                    dtde.dropComplete(true);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                dtde.dropComplete(false);
            }
        }
    }

    public void addFileToDocument(String filePath) {
        try {
            HTMLDocument doc = (HTMLDocument) getDocument();
            File file = new File(filePath);
            if (!file.exists()) {
                throw new IllegalArgumentException("Файл не найден: " + filePath);
            }

            String path = file.getAbsolutePath();
            String html = "Кто-то отправил файлик: "
                    + "<a href='file:///"
                    + path.replace("\\", "/").replace(" ", "%20") // для Windows
                    + "'>"
                    + file.getName()
                    + "</a><br>";

            doc.insertAfterEnd(
                    doc.getCharacterElement(doc.getLength()),
                    html
            );
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
