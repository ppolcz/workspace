#include "util.h"

#include "widgets/spinbox_expert.h"

namespace util {

    template<typename _QSpinBox, typename _Type>
    _QSpinBox* setupSpinBox (_QSpinBox* sb, _Type min, _Type step, _Type max, _Type value) {
        ((_QSpinBox*) sb)->setRange(min, max);
        ((_QSpinBox*) sb)->setSingleStep(step);
        ((_QSpinBox*) sb)->setValue(value);
        return (_QSpinBox*) sb;
    }

    template QSpinBox* setupSpinBox<QSpinBox, int> (QSpinBox* sb, int min, int step, int max, int value);
    template QDoubleSpinBox* setupSpinBox<QDoubleSpinBox, double> (QDoubleSpinBox* sb, double min, double step, double max, double value);

    template SpinBoxExpert* setupSpinBox<SpinBoxExpert, int> (SpinBoxExpert* sb, int min, int step, int max, int value);
}