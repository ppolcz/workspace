O = [0; 0; 0];

v = [1; 0; 0];

theta = pi / 4;
phi = pi / 3;

R1 = [
    cos(theta) -sin(theta) 0;
    sin(theta)  cos(theta) 0;
    0           0          1
]

R2 = [
    1         0         0       ;
    0         cos(phi) -sin(phi);
    0         sin(phi)  cos(phi)
]

R3 = [
    cos(theta)*cos(phi) -sin(theta)*cos(phi) -cos(theta)*sin(phi);
    sin(theta)*cos(phi)  cos(theta)*cos(phi) -sin(theta)*sin(phi);
    sin(phi)             sin(phi)             cos(phi)
]

R4 = [
    cos(phi)  sin(theta)*sin(phi) -cos(theta)*sin(phi);
    0         cos(theta)           sin(theta)         ;
    sin(phi) -sin(theta)*cos(phi)  cos(phi)*cos(theta)
]

figure(1), hold on, axis equal, axis([-1 1 -1 1 -1 1]);

plotVector(O,v);
%plotVector(O,R1*v);
%plotVector(O,R2*R1*v);
plotVector(O,R3*v);
%plotVector(O,R4*v);