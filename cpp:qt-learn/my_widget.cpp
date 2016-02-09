/* 
 * File:   MyWidget.cpp
 * Author: deva
 * 
 * Created on February 11, 2014, 5:42 PM
 */

#include "my_widget.h"
#include <iostream>

MyWidget::MyWidget (QWidget * parent) : QWidget(parent) { 
    std::cout << "MyWidget::MyWidget\n";
    connect(this, &MyWidget::triggerOK, &MyWidget::staticslot);
    emit triggerOK();
}

MyWidget::~MyWidget () { 
    std::cout << "MyWidget::~MyWdget\n";
}

void MyWidget::newInstance () {
//    MyWidget * widget = new MyWidget(this);
//    delete widget;
}

void MyWidget::staticslot() {
    std::cout << "staticSlot\n";
}