/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package surfacesandregions;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;
import surfacesandregions.MultiPosGrid;
import surfacesandregions.RegionManager;

/**
 *
 * @author Henry
 */
class RegionTableModel extends AbstractTableModel implements ListDataListener {

   private final String[] COLUMNS = {"Name", "# Rows", "# Cols", "Width (µm)", "Height (µm)"};
   private RegionManager manager_;
   
   public RegionTableModel(RegionManager manager) {
      manager_ = manager;
   }
   
   @Override
   public int getRowCount() {
      return manager_.getNumberOfRegions();
   }
   
   @Override
   public String getColumnName(int index) {
      return COLUMNS[index];
   }

   @Override
   public int getColumnCount() {
      return COLUMNS.length;
   }

   @Override
   public boolean isCellEditable(int rowIndex, int colIndex) {
      if (colIndex == 0) {
         return true;
      }
      return false;
   }
   
   @Override 
   public void setValueAt(Object value, int row, int col) {
      if (col == 0) {
         manager_.getRegion(row).rename((String) value);
      }
   }
   
   @Override
   public Object getValueAt(int rowIndex, int columnIndex) {
      MultiPosGrid region = manager_.getRegion(rowIndex);
      if (columnIndex == 0) {
         return manager_.getRegion(rowIndex).getName();
      } else if (columnIndex == 1) {
         return region.numRows();
      } else if (columnIndex == 2) {
         return region.numCols();
      } else if (columnIndex == 3) {
         return region.getWidth_um();
      } else {
         return region.getHeight_um();
      }
   }

   @Override
   public void intervalAdded(ListDataEvent e) {
      this.fireTableDataChanged();
   }

   @Override
   public void intervalRemoved(ListDataEvent e) {
      this.fireTableDataChanged();
   }

   @Override
   public void contentsChanged(ListDataEvent e) {
      this.fireTableDataChanged();
   }
   
}
