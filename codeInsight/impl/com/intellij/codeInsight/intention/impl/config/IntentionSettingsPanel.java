package com.intellij.codeInsight.intention.impl.config;

import com.intellij.ide.ui.search.OptionDescription;
import com.intellij.ide.ui.search.SearchUtil;
import com.intellij.ide.ui.search.SearchableOptionsRegistrar;
import com.intellij.ide.ui.search.SearchableOptionsRegistrarImpl;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.options.ex.GlassPanel;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.GuiUtils;
import org.jetbrains.annotations.NonNls;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IntentionSettingsPanel {
  private JPanel myPanel;
  private final IntentionSettingsTree myIntentionSettingsTree;
  private final IntentionDescriptionPanel myIntentionDescriptionPanel = new IntentionDescriptionPanel();

  private JPanel myTreePanel;
  private JPanel myDescriptionPanel;
  private GlassPanel myGlassPanel;

  public IntentionSettingsPanel() {
    myIntentionSettingsTree = new IntentionSettingsTree() {
      protected void selectionChanged(Object selected) {
        if (selected instanceof IntentionActionMetaData) {
          IntentionActionMetaData actionMetaData = (IntentionActionMetaData)selected;
          intentionSelected(actionMetaData);
        }
        else {
          categorySelected((String)selected);
        }
      }

      protected List<IntentionActionMetaData> filterModel(String filter, final boolean force) {
        final List<IntentionActionMetaData> list = IntentionManagerSettings.getInstance().getMetaData();
        if (filter == null || filter.length() == 0) return list;
        List<Set<String>> keySetList = new ArrayList<Set<String>>();
        final SearchableOptionsRegistrar optionsRegistrar = SearchableOptionsRegistrar.getInstance();
        final Set<String> words = optionsRegistrar.getProcessedWords(filter);
        for (String word : words) {
          final Set<OptionDescription> descriptions = ((SearchableOptionsRegistrarImpl)optionsRegistrar).getAcceptableDescriptions(word);
          Set<String> keySet = new HashSet<String>();
          if (descriptions != null) {
            for (OptionDescription description : descriptions) {
              keySet.add(description.getPath());
            }
          }
          keySetList.add(keySet);
        }
        List<IntentionActionMetaData> result = new ArrayList<IntentionActionMetaData>();
        for (IntentionActionMetaData metaData : list) {
          if (isIntentionAccepted(metaData, filter, force, keySetList)){
            result.add(metaData);
          }
        }
        final Set<String> filters = SearchableOptionsRegistrar.getInstance().getProcessedWords(filter);
        if (force && result.isEmpty()){
          if (filters.size() > 1){
            result = filterModel(filter, false);
          }
        }
        return result;
      }
    };
    myTreePanel.setLayout(new BorderLayout());
    myTreePanel.add(myIntentionSettingsTree.getComponent(), BorderLayout.CENTER);

    GuiUtils.replaceJSplitPaneWithIDEASplitter(myPanel);

    myDescriptionPanel.setLayout(new BorderLayout());
    myDescriptionPanel.add(myIntentionDescriptionPanel.getComponent(), BorderLayout.CENTER);

    myGlassPanel = new GlassPanel(myPanel);
  }

  private void intentionSelected(IntentionActionMetaData actionMetaData) {
    myIntentionDescriptionPanel.reset(actionMetaData,
                                      myIntentionSettingsTree.getFilter());
  }

  private void categorySelected(String intentionCategory) {
    myIntentionDescriptionPanel.reset(intentionCategory);
  }

  public void reset() {
    myPanel.getRootPane().setGlassPane(myGlassPanel);

    List<IntentionActionMetaData> list = IntentionManagerSettings.getInstance().getMetaData();
    myIntentionSettingsTree.reset(list);
    SwingUtilities.invokeLater(new Runnable(){
      public void run() {
        myIntentionDescriptionPanel.init(myPanel.getWidth()/2);
      }
    });
  }

  public void apply() {
    myIntentionSettingsTree.apply();
  }

  public JPanel getComponent() {
    return myPanel;
  }

  public JTree getIntentionTree(){
    return myIntentionSettingsTree.getTree();
  }

  public boolean isModified() {
    return myIntentionSettingsTree.isModified();
  }

  public void dispose() {
    myIntentionSettingsTree.dispose();
    myIntentionDescriptionPanel.dispose();
  }

  private static boolean isIntentionAccepted(IntentionActionMetaData metaData, @NonNls String filter, boolean forceInclude,
                                             final List<Set<String>> keySetList) {
    if (StringUtil.containsIgnoreCase(metaData.myFamily, filter)) {
      return true;
    }
    for (String category : metaData.myCategory) {
      if (category != null && StringUtil.containsIgnoreCase(category, filter)) {
        return true;
      }
    }

    for (Set<String> keySet : keySetList) {
      if (keySet.contains(metaData.myFamily)) {
        if (!forceInclude) {
          return true;
        }
      }
      else {
        if (forceInclude) {
          return false;
        }
      }
    }
    return forceInclude;
  }

  public Runnable showOption(final SearchableConfigurable configurable, final String option) {
    return new Runnable() {
      public void run() {
        myIntentionSettingsTree.reset(myIntentionSettingsTree.filterModel(option, true));
        myIntentionSettingsTree.setFilter(option);
        SearchUtil.lightOptions(configurable, myPanel, option, myGlassPanel).run();
      }
    };
  }

  public void clearSearch() {
    myGlassPanel.clear();
  }
}
