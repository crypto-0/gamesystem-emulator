package hermes.shared;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableColumnModel;

import java.awt.Color;
import java.awt.Dimension;

public class Ram extends JPanel{
  private short[] memory ;//= new short[100];
  private final int PWIDTH = 200;
  private final int PHEIGHT = 200;
  private JTable table;
  private TableColumnModel tableModel ;
  public Ram(int size){
    this.memory = new short[size];
    table = new JTable(memory.length,4);
    tableModel = table.getColumnModel();
    this.setVisible(true);
    tableModel.getColumn(0).setPreferredWidth(38);
    tableModel.getColumn(1).setPreferredWidth(20);
    tableModel.getColumn(2).setPreferredWidth(30);
    tableModel.getColumn(3).setPreferredWidth(60);
    table.setShowGrid(false);
    table.setTableHeader(null);
    table.setBackground(Color.BLACK);
    table.setForeground(Color.LIGHT_GRAY);
    JScrollPane jsp = new JScrollPane(table);
    this.setBackground(Color.BLACK);
    jsp.setVisible(true);
    jsp.setPreferredSize(new Dimension(PWIDTH,PHEIGHT));
    jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    add(jsp);
    TitledBorder border = new TitledBorder("Memory");
    border.setTitlePosition(TitledBorder.TOP);
    setBorder(border);
    table.setFocusable(false);
  }

  public short read(int address){
    return memory[address];
  }
  public void write(int address,short data){
    memory[address] = data;
  }
  public void render(){
    for(int a=0; a<this.memory.length; a++){
      String memoryIndex = String.format("%04X:", a);
      String memoryHex = String.format("%02X", this.memory[a]);
      String memoryDec = String.format("%03d", this.memory[a]);
      String memoryBin = String.format("%8s", Integer.toBinaryString(this.memory[a])).replace(" ","0");
      table.setValueAt(memoryIndex,a,0);
      table.setValueAt(memoryHex, a, 1);
      table.setValueAt(memoryDec, a, 2);
      table.setValueAt(memoryBin, a, 3);
    }
  }
}
