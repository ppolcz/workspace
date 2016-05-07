/* 
 * File:   main.cpp
 * Author: Péter Polcz <ppolcz@gmail.com>
 *
 * Created on February 13, 2015, 12:16 PM
 */

extern int main_gradient(int argc, char **argv);
extern int main_cdp(int argc, char **argv);

int main(int argc, char** argv)
{
    return main_gradient(argc, argv);
    return main_cdp(argc, argv);
}

