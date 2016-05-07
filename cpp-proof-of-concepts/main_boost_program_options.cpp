/* 
 * File:   main_boost_program_options.cpp
 * Author: Péter Polcz <ppolcz@gmail.com>
 *
 * Created on February 12, 2015, 10:16 PM
 */

#include <polcz/pczexcept.h>
#include <polcz/pczdebug.h>
#include <boost/program_options.hpp>

static const char * op(const std::string & s, const std::string & l)
{
    return (l + "," + s).c_str();
}
#define EXP_TOSTR(s...) TOSTR(s)
#define TOSTR(s...) #s

#define __SCENE_1       1
#define __SCENE_2       2
#define __SCENE_3       4

// multiple arguments:
// http://stackoverflow.com/questions/8175723/vector-arguments-in-boost-program-options

int main_boost_po(int argc, char** argv)
{
    std::string op_help = "help";
    std::string op_view = "view";
    std::string op_texture = "texture";
    std::string op_cloud = "cloud";
    std::string op_utm = "utmzone";
    std::string op_scene = "scene";
    std::string op_obj = "genobj";
    
    std::string val_glm = "";

    try
    {
        namespace po = boost::program_options;

        po::options_description desc("Options");
        desc.add_options()(op("h", op_help), "Print this help messages");
        desc.add_options()(op("v", op_view), po::value<std::string>(), "View input .glm textured city model");
        desc.add_options()(op("t", op_texture), po::value<std::string>(), "Input texture image");
        desc.add_options()(op("c", op_cloud), po::value<std::string>(), "Input .pcd cloud");
        desc.add_options()(op("o", op_obj), "Generate .obj file");
        desc.add_options()(op("z", op_utm), po::value<int>(), "Specify UTM zone");
        desc.add_options()(op("s", op_scene), po::value<int>(),
            "Specify scene mode"
            "\n" EXP_TOSTR(__SCENE_1) ": textured polygon"
            "\n" EXP_TOSTR(__SCENE_2) ": textured points of vegetation"
            "\n" EXP_TOSTR(__SCENE_3) ": textured points of the whole cloud"
            );

        po::variables_map vm;
        try
        {
            po::store(po::parse_command_line(argc, argv, desc), vm);

            for (int i = 0; vm.count(op_help); ++i, std::cout << "Texture generator and GL viewer\n"
                "Author: Polcz Péter <ppolcz@gmail.com>\n" << desc << ""
                << std::endl) if (i) return EXIT_SUCCESS;

            if (vm.count(op_obj));

            if (vm.count(op_scene));

            if (vm.count(op_utm));

            if (vm.count(op_view));
            
            if (vm.count(op_texture) && vm.count(op_cloud));

            po::notify(vm);
        }
        catch (po::error& e)
        {
            std::cerr << "ERROR: " << PCZ_DEBUG_INFO << "\nMessage: " << e.what() << std::endl << std::endl;
            std::cerr << desc << std::endl;
            return EXIT_FAILURE;
        }
    }
    catch (PException &e)
    {
        e.addStackEntry(PCZ_DEBUG_INFO);
        std::cerr << e.what() << std::endl;
    }
    catch (std::exception& e)
    {
        std::cerr << "Unhandled Exception reached the top of main\nMessage: "
            << e.what() << std::endl;
        return EXIT_FAILURE;
    }

    if (!val_glm.empty());

    return 0;
}

