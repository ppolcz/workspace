#ifndef QT_THREADDING_H
#define	QT_THREADDING_H

#include <iostream>

#include <QObject>
#include <QThread>

class Worker : public QObject {

    Q_OBJECT
public slots:

    void doWork (const QString & msg) {
        std::cout << "doWork\n";
        emit resultReady(msg);
        return;
    }

    void doWork () {
        std::cout << "doWork(void)\n";
        emit resultReady("whahoo");
    }

    void doWork (int i) {
        std::cout << QString("doWork(%1)\n").arg(i).toStdString();
    }
    
signals:
    void resultReady (const QString &result);
};

// ----------------------------------------------------------

class Controller : public QObject {
    Q_OBJECT
    QThread workerThread;
public:

    Controller () {
        {
            Worker *worker = new Worker;
            worker->moveToThread(&workerThread);
            connect(&workerThread, SIGNAL(finished()), worker, SLOT(deleteLater()));
            connect(worker, SIGNAL(resultReady(QString)), this, SLOT(handleResults(QString)));
            workerThread.start();

            //            QMetaObject::invokeMethod(worker, "doWork", Qt::QueuedConnection, QGenericArgument("const QString &", (void*)"Csinaljad oreg"));
            QMetaObject::invokeMethod(worker, "doWork", Qt::QueuedConnection);
            QMetaObject::invokeMethod(worker, "doWork", Qt::QueuedConnection, Q_ARG(const QString &, "Csinald"));
            QMetaObject::invokeMethod(worker, "doWork", Qt::QueuedConnection, Q_ARG(int, 1));
            QMetaObject::invokeMethod(worker, "doWork", Qt::QueuedConnection, Q_ARG(int, 3));
            QMetaObject::invokeMethod(worker, "doWork", Qt::QueuedConnection, Q_ARG(int, 2));
            QMetaObject::invokeMethod(worker, "doWork", Qt::QueuedConnection, Q_ARG(int, 5));
            QMetaObject::invokeMethod(worker, "doWork", Qt::QueuedConnection, Q_ARG(int, 4));
            QMetaObject::invokeMethod(worker, "doWork", Qt::QueuedConnection, Q_ARG(int, 6));

        }
        {
            Worker *worker = new Worker;
            worker->moveToThread(&workerThread);
            connect(&workerThread, SIGNAL(finished()), worker, SLOT(deleteLater()));
            connect(this, SIGNAL(operate(QString)), worker, SLOT(doWork(QString)));
            connect(worker, SIGNAL(resultReady(QString)), this, SLOT(handleResults(QString)));
            workerThread.start();
            // trigger:
            emit operate("operaljal...");
        }
    }

    ~Controller () {
        QThread::currentThread()->wait(1000);
        workerThread.quit();
        std::cout << "~Controller\n";
        workerThread.wait();
        std::cout << "~Controller\n";
    }
public slots:

    void handleResults (const QString & result) {
        std::cout << result.toStdString() << std::endl;
    }

signals:
    void operate (const QString &);
};

// -----------------------------------------------------------

class QtThreadding {
public:
    QtThreadding ();
    QtThreadding (const QtThreadding& orig);
    virtual ~QtThreadding ();
private:

};

#endif	/* QT_THREADDING_H */

