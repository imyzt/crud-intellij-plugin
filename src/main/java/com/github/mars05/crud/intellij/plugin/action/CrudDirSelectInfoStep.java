package com.github.mars05.crud.intellij.plugin.action;

import com.github.mars05.crud.intellij.plugin.util.SelectionContext;
import com.google.common.base.Preconditions;
import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.search.GlobalSearchScope;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static com.github.mars05.crud.intellij.plugin.util.SelectionContext.*;

/**
 * @author xiaoyu
 */
public class CrudDirSelectInfoStep extends ModuleWizardStep {
    private JPanel myMainPanel;
    private JComboBox myFrameComboBox;
    private JCheckBox myControllerCheckBox;
    private JTextField myControllerField;
    private JCheckBox myServiceCheckBox;
    private JTextField myServiceField;
    private JCheckBox myDaoCheckBox;
    private JTextField myDaoField;
    private JPanel myMapperField;
    private JButton myControllerChoose;
    private JButton myServiceChoose;
    private JButton myDaoChoose;
    private JCheckBox myModelCheckBox;
    private JTextField myModelField;
    private JButton myModelChoose;
    private JPanel myPackagePanel;
    // private JLabel myMapperLabel;
    private JCheckBox mapperCheckBox;
    private JTextField authorField;
    private JCheckBox lombokCheckBox;
    private JCheckBox fillFieldCheckBox;

    private Project myProject;
    private Module myModule;

    public CrudDirSelectInfoStep(Project project, Module module) {

        myProject = project;
        myModule = module;
        myControllerField.setText(SelectionContext.getControllerPackage());
        myServiceField.setText(SelectionContext.getServicePackage());
        myDaoField.setText(SelectionContext.getDaoPackage());
        myModelField.setText(SelectionContext.getModelPackage());
        authorField.setText(SelectionContext.getAuthor());
        lombokCheckBox.setSelected(SelectionContext.getLombokSelected());

        ((TextFieldWithBrowseButton) myMapperField).setText(SelectionContext.getMapperDir());

        myControllerCheckBox.addChangeListener(e -> checkBoxSetup(myControllerCheckBox.isSelected()));
        myServiceCheckBox.addChangeListener(e -> checkBoxSetup(myServiceCheckBox.isSelected()));
        myDaoCheckBox.addChangeListener(e -> checkBoxSetup(myDaoCheckBox.isSelected()));
        mapperCheckBox.addChangeListener(e -> checkBoxSetup(mapperCheckBox.isSelected()));
        myModelCheckBox.addChangeListener(e -> checkBoxSetup(myModelCheckBox.isSelected()));

        myFrameComboBox.addItemListener(e -> switchFrame());

        myControllerChoose.addActionListener(new AbstractAction() {

            // TODO: 2020/7/5 增加基础包的检查, 不能选中低于基础包的路径
            @Override
            public void actionPerformed(ActionEvent e) {
                PackageChooserDialog dialog = new PackageChooserDialog("Controller Package Choose", project);
                dialog.selectPackage(myControllerField.getText());
                if (dialog.showAndGet()) {
                    PsiPackage selectedPackage = dialog.getSelectedPackage();
                    myControllerField.setText(selectedPackage.getQualifiedName());
                }
            }
        });
        myServiceChoose.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PackageChooserDialog dialog = new PackageChooserDialog("Service Package Choose", project);
                dialog.selectPackage(myServiceField.getText());
                if (dialog.showAndGet()) {
                    PsiPackage selectedPackage = dialog.getSelectedPackage();
                    myServiceField.setText(selectedPackage.getQualifiedName());
                }
            }
        });
        myDaoChoose.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PackageChooserDialog dialog = new PackageChooserDialog("Dao Package Choose", project);
                dialog.selectPackage(myDaoField.getText());
                if (dialog.showAndGet()) {
                    PsiPackage selectedPackage = dialog.getSelectedPackage();
                    myDaoField.setText(selectedPackage.getQualifiedName());
                }
            }
        });
        myModelChoose.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PackageChooserDialog dialog = new PackageChooserDialog("Model Package Choose", project);
                dialog.selectPackage(myModelField.getText());
                if (dialog.showAndGet()) {
                    PsiPackage selectedPackage = dialog.getSelectedPackage();
                    myModelField.setText(selectedPackage.getQualifiedName());
                }
            }
        });
        switchFrame();
    }

    @Override
    public JComponent getComponent() {
        return myMainPanel;
    }

    @Override
    public boolean validate() throws ConfigurationException {
        if (!myModelCheckBox.isSelected() && !myDaoCheckBox.isSelected() && !myServiceCheckBox.isSelected() && !myControllerCheckBox.isSelected()) {
            throw new ConfigurationException("未选择需要生成的文件");
        }
        JavaPsiFacade facade = JavaPsiFacade.getInstance(myProject);

        int selectedIndex = myFrameComboBox.getSelectedIndex();
        int ormType;

        // 检查依赖包是否存在
        if (selectedIndex == MYBATIS_PLUS) {
            try {
                Preconditions.checkNotNull(facade.findClass("org.apache.ibatis.session.SqlSession", GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(myModule)),
                        "org.apache.ibatis.session.SqlSession 未找到");
                Preconditions.checkNotNull(facade.findClass("org.mybatis.spring.SqlSessionFactoryBean", GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(myModule)),
                        "org.mybatis.spring.SqlSessionFactoryBean 未找到");
            } catch (Exception e) {
                throw new ConfigurationException(e.getMessage(), "缺少依赖");
            }
            ormType = MYBATIS_PLUS;
        } else if (selectedIndex == MYBATIS) {
            try {
                Preconditions.checkNotNull(facade.findClass("org.apache.ibatis.session.SqlSession", GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(myModule)),
                        "org.apache.ibatis.session.SqlSession 未找到");
                Preconditions.checkNotNull(facade.findClass("org.mybatis.spring.SqlSessionFactoryBean", GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(myModule)),
                        "org.mybatis.spring.SqlSessionFactoryBean 未找到");
                Preconditions.checkNotNull(facade.findClass("com.github.pagehelper.Page", GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(myModule)),
                        "com.github.pagehelper.Page 未找到");
            } catch (Exception e) {
                throw new ConfigurationException(e.getMessage(), "缺少依赖");
            }
            ormType = MYBATIS;
        } else {
            try {
                Preconditions.checkNotNull(facade.findClass("javax.persistence.Table", GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(myModule)),
                        "javax.persistence.Table 未找到");
                Preconditions.checkNotNull(facade.findClass("org.springframework.data.jpa.repository.JpaRepository", GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(myModule)),
                        "org.springframework.data.jpa.repository.JpaRepository 未找到");
                Preconditions.checkNotNull(facade.findClass("org.springframework.data.domain.Page", GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(myModule)),
                        "org.springframework.data.domain.Page 未找到");
            } catch (Exception e) {
                throw new ConfigurationException(e.getMessage(), "缺少依赖");
            }
            ormType = JPA;
        }

        SelectionContext.setOrmType(ormType);

        //先清空所有包
        SelectionContext.setControllerPackage(null);
        SelectionContext.setServicePackage(null);
        SelectionContext.setDaoPackage(null);
        SelectionContext.setMapperDir(null);
        SelectionContext.setModelPackage(null);
        if (myControllerCheckBox.isSelected()) {
            SelectionContext.setControllerPackage(myControllerField.getText());
        }
        if (myServiceCheckBox.isSelected()) {
            SelectionContext.setServicePackage(myServiceField.getText());
        }
        if (myDaoCheckBox.isSelected()) {
            SelectionContext.setDaoPackage(myDaoField.getText());
            // Mybatis需要生成Mapper文件
            if (MYBATIS == selectedIndex || MYBATIS_PLUS == selectedIndex) {
                SelectionContext.setMapperDir(((TextFieldWithBrowseButton) myMapperField).getText());
            }
        }
        if (myModelCheckBox.isSelected()) {
            SelectionContext.setModelPackage(myModelField.getText());
        }
        SelectionContext.setAuthor(authorField.getText());
        SelectionContext.setLombokSelected(lombokCheckBox.isSelected());
        SelectionContext.setFillFieldSelected(fillFieldCheckBox.isSelected());

        return super.validate();
    }

    private void switchFrame() {
        // 当选中JPA时, 不需要显示Mapper的框
        if (JPA == myFrameComboBox.getSelectedIndex()) {
            mapperCheckBox.setVisible(false);
            myMapperField.setVisible(false);
        } else {
            mapperCheckBox.setVisible(true);
            myMapperField.setVisible(true);
        }
    }

    private void checkBoxSetup(boolean selected) {
        if (selected) {
            //处理选中
            if (myControllerCheckBox.isSelected()) {
                myModelCheckBox.setSelected(true);
                myDaoCheckBox.setSelected(true);
                mapperCheckBox.setSelected(true);
                myServiceCheckBox.setSelected(true);
            } else if (myServiceCheckBox.isSelected()) {
                myModelCheckBox.setSelected(true);
                myDaoCheckBox.setSelected(true);
                mapperCheckBox.setSelected(true);
            } else if (myDaoCheckBox.isSelected()) {
                mapperCheckBox.setSelected(true);
                myModelCheckBox.setSelected(true);
            } else if (mapperCheckBox.isSelected()) {
                myDaoCheckBox.setSelected(true);
                myModelCheckBox.setSelected(true);
            }
        } else {
            if (!myModelCheckBox.isSelected()) {
                //处理没选中
                myDaoCheckBox.setSelected(false);
                mapperCheckBox.setSelected(false);
                myServiceCheckBox.setSelected(false);
                myControllerCheckBox.setSelected(false);
            } else if (!myDaoCheckBox.isSelected()) {
                myServiceCheckBox.setSelected(false);
                myControllerCheckBox.setSelected(false);
                mapperCheckBox.setSelected(false);
            } else if (!mapperCheckBox.isSelected()) {
                myServiceCheckBox.setSelected(false);
                myControllerCheckBox.setSelected(false);
                myDaoCheckBox.setSelected(false);
            } else if (!myServiceCheckBox.isSelected()) {
                myControllerCheckBox.setSelected(false);
            }
        }

    }

    @Override
    public void updateDataModel() {

    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        TextFieldWithBrowseButton browseButton = new TextFieldWithBrowseButton();
        browseButton.addBrowseFolderListener(new TextBrowseFolderListener(new FileChooserDescriptor(false, true, false, false, false, false)));
        this.myMapperField = browseButton;
    }
}
