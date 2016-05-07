/* 
 * File:   dem_viewer.h
 * Author: polpe
 *
 * Created on February 26, 2014, 12:17 PM
 */

#ifndef DEM_VIEWER_H
#define	DEM_VIEWER_H

#include <QGLWidget>

class DemViewer : public QGLWidget {
public:
    explicit DemViewer (QWidget * parent = 0);
    virtual ~DemViewer ();

protected:
    virtual void initializeGL ();
    virtual void resizeGL (int w, int h);
    virtual void paintGL ();

private:
    void p_printMatrix (GLenum matrix_name, const char * comment = "the matrix");
    void p_drawCoordinateSystem ();
    void p_loadTexture ();

    double p_camPos = -300.0f;
    double p_zNear = -5.0f;
    double p_zFar = 5.0f;

    double p_minDemX = 347644.5;
    double p_minDemY = 5256933.5;
    double p_maxDemX = 360673.5;
    double p_maxDemY = 5268875.5;

    double p_centerDemX = (p_minDemX + p_maxDemX) / 2.0;
    double p_centerDemY = (p_minDemY + p_maxDemY) / 2.0;

    double p_scaleDemX = 200.0 / (p_maxDemX - p_minDemX);
    double p_scaleDemY = 200.0 / (p_maxDemY - p_minDemY);

    double p_minClipX = 353551;
    double p_maxClipX = 354022;
    double p_minClipY = 5260860;
    double p_maxClipY = 5261230;

    double m_vertexBuffer[12];
    double * mp_vertexBuffer;
    
    static const float s_vertexBuffer[];
    static const float s_colorBuffer[];
    static const float s_textureBuffer[];

    QImage m_textureImage;
    GLuint m_textureID;
};

#define P_CLEAR_RED 1
#define P_CLEAR_GREEN 1 
#define P_CLEAR_BLUE 1 
#define P_CLEAR_ALPHA 0

#define P_DEFAULT_FOV_DEG 60.0f

#endif	/* DEM_VIEWER_H */

