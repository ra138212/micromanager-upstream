///////////////////////////////////////////////////////////////////////////////
//FILE:          MultichannelModes.java
//PROJECT:       Micro-Manager 
//SUBSYSTEM:     ASIdiSPIM plugin
//-----------------------------------------------------------------------------
//
// AUTHOR:       Nico Stuurman, Jon Daniels
//
// COPYRIGHT:    University of California, San Francisco, & ASI, 2013
//
// LICENSE:      This file is distributed under the BSD license.
//               License text is included with the source distribution.
//
//               This file is distributed in the hope that it will be useful,
//               but WITHOUT ANY WARRANTY; without even the implied warranty
//               of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//
//               IN NO EVENT SHALL THE COPYRIGHT OWNER OR
//               CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
//               INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES.

package org.micromanager.asidispim.Data;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import org.micromanager.asidispim.Utils.DevicesListenerInterface;


/**
 * Class that holds utilities related to multicolor sequence order
 * (e.g. change color slice by slice, etc.).
 * 
 * @author Jon
 */
public class MultichannelModes {
   
   private final Devices devices_;   // object holding information about selected/available devices
   private final Properties props_;  // object handling all property read/writes
   private final Devices.Keys devKey_;
   private final Properties.Keys propKey_;
   
   /**
    * Enum to store the acquisition mode along with associated preference code.
    * Wanted to store integer code instead of string so that the mode descriptions
    * can be easily changed. 
    */
   public static enum Keys {
      VOLUME("Every volume", 1),
//      SIDE("Every side", 2),
      SLICE("Every slice", 3),
      NONE("None", 0);
      private final String text;
      private final int prefCode;
      Keys(String text, int prefCode) {
         this.text = text;
         this.prefCode = prefCode;
      }
      @Override
      public String toString() {
         return text;
      }
      public int getPrefCode() {
         return prefCode;
      }
   };
   
   public MultichannelModes(Devices devices, Properties props,
         Devices.Keys devKey, Properties.Keys propKey, Keys defaultKey) {
      devices_ = devices;
      props_ = props;
      devKey_ = devKey;
      propKey_ = propKey;
      
      // make sure it gets set to something
      if (!props_.hasProperty(devKey, propKey)) {
         props_.setPropValue(devKey,
               propKey, defaultKey.getPrefCode());
      }
   }
   
   /**
    * @return null if prefCode not found, or the Key if it is
    */
   public static Keys getKeyFromPrefCode(int prefCode) {
      for (Keys k : Keys.values()) {
         if (k.getPrefCode() == prefCode) {
            return k;
         }
      }
      return null;
   }
   
   
   public JComboBox getComboBox() {
      JComboBox jcb = new JComboBox();
      ActionListener l = new ComboBoxListener(jcb);
      jcb.addActionListener(l);
      // when devices are changed we want to regenerate the list
      devices_.addListener((DevicesListenerInterface) l);
      return jcb;
   }
   
   private class ComboBoxListener implements ActionListener, DevicesListenerInterface {
      
      private final JComboBox jcb_;
      
      public ComboBoxListener(JComboBox jcb) {
         jcb_ = jcb;
         updateSelections();  // do initial rendering
      }
      
      @Override
      public void actionPerformed(ActionEvent ae) {
         props_.setPropValue(devKey_, propKey_,
               ((Keys) jcb_.getSelectedItem()).getPrefCode());
      }
      
      /**
       * called whenever one of the devices is changed in the "Devices" tab
       */
      @Override
      public void devicesChangedAlert() {
       updateSelections();
      }
      
      /**
       * Resets the items in the combo box based on devices available.
       * Besides being called on original combo box creation, it is called 
       * whenever something in the devices tab is changed
       */
      private void updateSelections() {
         // save the existing selection if it exists
         int origCode = props_.getPropValueInteger(devKey_, propKey_);
         
         DefaultComboBoxModel cbModel = new DefaultComboBoxModel();
         
         List<Keys> validModeKeys = getValidModeKeys();
         Keys origItem = null;
         for (Keys k : validModeKeys) {
            cbModel.addElement(k);
            if (k.getPrefCode() == origCode) {
               origItem = k;
            }
         }
         jcb_.setModel(cbModel);
         if (origItem != null) {
            jcb_.setSelectedItem(origItem);
         }
      }//updateSelections


      /**
       * Returns whatever modes are available.
       * Can be expanded in the future
       * @return
       */
      private List<Keys> getValidModeKeys() {
         List<Keys> keyList = new ArrayList<Keys>();
         keyList.add(Keys.VOLUME);
//         keyList.add(Keys.SIDE);
         // PLogic required to change slice by slice
         if (devices_.isValidMMDevice(Devices.Keys.PLOGIC)) {
            keyList.add(Keys.SLICE);
         }
         return keyList;
      }

      
   }

}
