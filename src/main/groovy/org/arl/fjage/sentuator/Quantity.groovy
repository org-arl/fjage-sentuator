package org.arl.fjage.sentuator

/**
 * Measured quantity with units.
 */
@groovy.transform.CompileStatic
class Quantity {

  final Object value
  final String units

  Quantity(Object value) {
    this.value = value
    this.units = null
  }

  Quantity(Object value, String units) {
    this.value = value
    this.units = units
  }

  @Override
  boolean equals(Object q) {
    return (q instanceof Quantity && q.value == value && q.units == q.units)
  }

  @Override
  String toString() {
    if (value == null) return null
    String vstr = value.class.isArray() ? '<data>' : value.toString()
    if (units == null) return vstr
    return "$vstr $units"
  }

}
