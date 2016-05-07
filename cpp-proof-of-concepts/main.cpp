/*
 * File:   main.cpp
 * Author: Péter Polcz <ppolcz@gmail.com>
 *
 * Created on January 25, 2015, 10:46 PM
 */

#include <polcz/pczutil.h>

void main_virtual_things();
void main_virtual_destructors();
void main_dynamic_link_constructor();
void main_atexit_callback();
void main_thread_1();
void main_signals();
void main_move();
void main_move_2();
void main_boost_po(int argc, char** argv);
void main_friend();
void main_template_specialization();
void main_multiple_inheritance();
void main_eigen();

int main(int argc, char *argv[])
{
//    main_boost_po(argc, argv);
//    main_virtual_things();
//    main_virtual_destructors();
//    main_dynamic_link_constructor();
//    main_atexit_callback();
//    main_signals();
//    main_thread_1();
//    main_move();
//    main_move_2();
//    main_friend();
//    main_template_specialization();
//    main_multiple_inheritance();
    main_eigen();
    
    return 0;
    
//    PCZ_UNUSED(argc, argv)
}
