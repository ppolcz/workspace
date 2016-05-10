/*
 * File:   main.cpp
 * Author: polpe
 *
 * Created on February 12, 2014, 1:13 AM
 * 
 * export QMAKE=qmake ; make -f /home/polpe/Repositories/GitHub/qt-widgets/Makefile -C /home/polpe/Repositories/GitHub/qt-widgets/ ; rm dist/Debug/GNU-Linux-x86/project-szakdolgozat ; make ; dist/Debug/GNU-Linux-x86/project-szakdolgozat
 * 
 */

#include <iostream>
#include <typeinfo>
#include <boost/format.hpp>

#include <QStandardItem>
#include <QStandardItemModel>
#include <QStyledItemDelegate>
#include <qt4/QtCore/qabstractitemmodel.h>

#include "util/qt_includes.h"

#include "qt_threadding.h"
#include "my_widget.h"

// from static library
#include "widgets/openwidget.h"
#include "widgets/inputbox.h"
#include "widgets/listview.h"
#include "widgets/treeview.h"
#include "widgets/droppablelineedit.h"
#include "widgets/appendablelineedit.h"
#include "widgets/filebrowserview.h"
#include "widgets/formtoolbox.h"
#include "layouts/inputslayout.h"

#include "windows/cropdem_main.h"

class QPersonItem : public QStandardItem {

    Q_OBJECT
public:

    QPersonItem () : QStandardItem () { }

private:
    QString m_cert;
};

class QMyItemDelegate : public QStyledItemDelegate {

    virtual QWidget* createEditor (QWidget* parent, const QStyleOptionViewItem& option, const QModelIndex& index) const {
        qDebug() << index.row() << "\nWhahoo\n";
        return QStyledItemDelegate::createEditor(parent, option, index);
    }
};

int main (int argc, char * argv[]) {
    // initialize resources, if needed
    // Q_INIT_RESOURCE(resfile);

    { // creating custom ListView s
        
        QApplication app(argc, argv);

        QStandardItem * item;
        QList<QStandardItem*> items;
        QList<QStandardItem*> items1;

        item = new QStandardItem("Whahoo");
        item->setCheckable(true);
        item->setCheckState(Qt::Checked);
        items.push_back(item);

        item = new QStandardItem("Masik");
        item->setCheckable(true);
        items.push_back(item);

        item = new QStandardItem("Harmadik");
        item->setCheckable(true);
        items1.push_back(item);

        item = new QStandardItem("Negyedik");
        item->setCheckable(true);
        items1.push_back(item);

        qDebug() << items.size() << "\n";
        QStandardItemModel * model = new QStandardItemModel();
        model->appendColumn(items);
        model->appendColumn(items1);

        QListView lw;
        //        lw.setItemDelegate(new QMyItemDelegate());
        lw.setModel(model);
        lw.setLayout(new QGridLayout);
        //        lw.setGridSize(QSize(model->rowCount(), model->columnCount()));
        lw.show();

        auto indexList = lw.selectionModel()->selectedIndexes();
        qDebug() << "model->rowCount() = " << model->rowCount();
        if (model->item(0, 0)->checkState() == Qt::Checked) qDebug() << "checked";
        else qDebug() << "NOT checked\n";
        if (model->item(1, 0)->checkState() == Qt::Checked) qDebug() << "checked";
        else qDebug() << "NOT checked\n";

        qDebug() << "indexList.size() = " << indexList.size();
        for (int i = 0; i < indexList.size(); ++i) {
            qDebug() << "checked: " << indexList[i].row() << ", " << indexList[i].column();
        }

        qDebug() << lw.gridSize().height() << " " << lw.gridSize().width();

        return app.exec();
    }

    {
        QApplication app(argc, argv);

        std::string valamit = "valami";
        std::cout << boost::format("|%d|%s|%f|\n") % 12 % valamit % 12.4;

        std::string filename = "/home/polpe/Resources/LiDAR-uj-rekonstrualva/vasarcsarnok-folott/parts/part_4/cloud.pcd";

        QDir dir(filename.c_str(), "cloud*");
        dir.cdUp();
        std::cout << dir.absolutePath().toStdString() << std::endl;
        QStringList parts = dir.entryList();

        for (int i = 0; i < parts.size(); ++i) {
            std::cout << parts[i].toStdString() << std::endl;
        }


        return 0;

        // test static connections
        // http://qt-project.org/wiki/New_Signal_Slot_Syntax
        MyWidget connectionTest;

        // testing QThread
        Controller();

        // test - parent free pointer
        MyWidget widget;
        widget.newInstance();
        widget.show();

        // testing OpenWidget
        OpenWidget openWidget;
        openWidget.show();

        // testing colored widget
        QWidget widgetColored;
        widgetColored.setGeometry(0, 0, 300, 100);
        widgetColored.setStyleSheet("background-color:black;");
        widgetColored.show();

        // testing model-view
        QSplitter *splitter = new QSplitter;
        // --
        QFileSystemModel *model = new QFileSystemModel;
        model->setRootPath(QDir::rootPath());
        // --
        QTreeView *tree = new QTreeView(splitter);
        tree->setModel(model);
        tree->setRootIndex(model->index(QDir::rootPath()));
        tree->setAcceptDrops(true);
        tree->setAllColumnsShowFocus(true);
        tree->setAnimated(true);
        tree->setAlternatingRowColors(true);
        tree->setDragEnabled(true);
        tree->setRootIsDecorated(true);
        tree->setDropIndicatorShown(true);
        tree->setExpandsOnDoubleClick(true);
        tree->setHeaderHidden(false);
        // --
        QListView *list = new QListView(splitter);
        list->setModel(model);
        list->setRootIndex(model->index(QDir::currentPath()));
        list->setDragEnabled(true);
        // --
        splitter->setWindowTitle("Two views onto the same file system model");
        splitter->show();

        // testing FSystemView
        FileBrowserView fs(new NavigableTreeView);
        fs.show();

        // testing navigable listview
        NavigableListView listview;
        listview.setModel(model);
        listview.setRootIndex(model->index(QDir::currentPath()));
        listview.setSelectionMode(QAbstractItemView::ExtendedSelection);
        listview.show();

        // testing DroppableLineEdit
        AppendableLineEdit edit;
        edit.show();

        // testing InputsLayout
        QWidget * iwidget = new QWidget;
        new InputsLayout(iwidget);

        // testing splitter
        QSplitter * splitter1 = new QSplitter;
        QListView *listview1 = new QListView;
        QTreeView *treeview1 = new QTreeView;
        QTextEdit *textedit1 = new QTextEdit;
        splitter1->addWidget(listview1);
        splitter1->addWidget(treeview1);
        splitter1->addWidget(textedit1);

        // testing InputBox
        QMainWindow mainWindow;
        QWidget layoutWidget;
        QGridLayout * widgetLayout = new QGridLayout(&layoutWidget);
        InputBox * inputBox = new InputBox();
        inputBox->setTitle("Proba");
        inputBox->addInput<QDoubleSpinBox>("new input");
        inputBox->addInput<QSpinBox>("integer input");
        inputBox->addInput<QLineEdit>("string input");
        QComboBox * combo = inputBox->addInput<QComboBox>("combo input");
        combo->addItem("Kutya");
        combo->addItem("Macska");
        combo->addItem("Baratsag");
        widgetLayout->addWidget(inputBox, 0, 0);
        widgetLayout->addWidget(iwidget, 0, 1);
        widgetLayout->addWidget(splitter1, 1, 0, 1, 2);
        mainWindow.setCentralWidget(&layoutWidget);
        mainWindow.show();

        //    CropDemMain window1;
        //    window1.show();

        //    QWidget* w = new QWidget;
        //    QVBoxLayout* l = new QVBoxLayout;
        //    w->setLayout(l);
        //    QPushButton* b = new QPushButton("hello");
        //    b->setSizePolicy(QSizePolicy::Expanding,QSizePolicy::Expanding);
        //    l->addWidget(b);
        //    w->show();    

        FormToolBox ftb;
        ftb.show();
        auto * cdb1 = new CropDemBox;
        cdb1->setTitle("");
        ftb.addItem(cdb1, "CropDemBox");
        ftb.addItem(new CropDemBox, "CropDemBox");
        ftb.addItem(new CropDemBox, "CropDemBox");

        return app.exec();
    }
}
