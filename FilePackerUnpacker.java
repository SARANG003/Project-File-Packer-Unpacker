import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class FilePackerUnpacker extends JFrame implements ActionListener 
{
    JLabel headerLabel, statusLabel;
    JTextArea textArea;
    JPanel controlPanel;
    JButton packButton, unpackButton;
    JFileChooser fileChooser;

    public FilePackerUnpacker() 
    {
        setTitle("File Packer and Unpacker");
        setLayout(new BorderLayout());
        headerLabel = new JLabel("File Packer and Unpacker", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        statusLabel = new JLabel("Status: Ready", JLabel.CENTER);
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(1, 2, 10, 10));

        packButton = new JButton("Pack Files");
        unpackButton = new JButton("Unpack Files");

        packButton.addActionListener(this);
        unpackButton.addActionListener(this);

        controlPanel.add(packButton);
        controlPanel.add(unpackButton);

        add(headerLabel, BorderLayout.NORTH);
        add(controlPanel, BorderLayout.CENTER);
        add(new JScrollPane(textArea), BorderLayout.SOUTH);
        add(statusLabel, BorderLayout.PAGE_END);

        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) 
    {
        if (e.getSource() == packButton) 
        {
            packFiles();
        } 
        else if (e.getSource() == unpackButton) 
        {
            unpackFiles();
        }
    }

    private void packFiles() 
    {
        fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        int option = fileChooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) 
        {
            File[] files = fileChooser.getSelectedFiles();
            String outputFileName = JOptionPane.showInputDialog(this, "Enter output file name:", "packedFile.dat");
            if (outputFileName != null && !outputFileName.trim().isEmpty()) 
            {
                try (FileOutputStream fout = new FileOutputStream(outputFileName)) 
                {
                    for (File file : files) 
                    {
                        if (file.exists()) 
                        {
                            try (FileInputStream fin = new FileInputStream(file)) 
                            {
                                String header = file.getName() + ":" + file.length() + ":";
                                fout.write(header.getBytes());
                                int i;
                                while ((i = fin.read()) != -1) 
                                {
                                    fout.write(i);
                                }
                                textArea.append("Packed: " + file.getName() + "\n");
                            }
                        }
                    }
                    statusLabel.setText("Status: Files Packed Successfully");
                } catch (Exception e) {
                    statusLabel.setText("Status: Error Packing Files");
                }
            }
        }
    }

    private void unpackFiles() 
    {
        fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) 
        {
            File packedFile = fileChooser.getSelectedFile();
            try (FileInputStream fin = new FileInputStream(packedFile)) 
            {
                int i;
                StringBuilder header = new StringBuilder();
                while ((i = fin.read()) != -1) 
                {
                    header.append((char) i);
                    if (header.toString().endsWith(":")) 
                    {
                        String[] parts = header.toString().split(":");
                        if (parts.length >= 2) 
                        {
                            String fileName = parts[0];
                            int fileSize = Integer.parseInt(parts[1]);
                            header.setLength(0); 
                            try (FileOutputStream fout = new FileOutputStream(fileName)) 
                            {
                                for (int j = 0; j < fileSize; j++) {
                                    fout.write(fin.read());
                                }
                                textArea.append("Unpacked: " + fileName + "\n");
                            }
                        }
                    }
                }
                statusLabel.setText("Status: Files Unpacked Successfully");
            } catch (Exception e) {
                statusLabel.setText("Status: Error Unpacking Files");
            }
        }
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(FilePackerUnpacker::new);
    }
}
