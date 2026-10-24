package vaadin.customfield

/** Parses the field value into the values of the individual inputs. */
typealias CustomFieldParseValueFn = (String) -> Array<kotlin.js.JsAny?>

/** Formats the values of the individual inputs into the field value. */
typealias CustomFieldFormatValueFn = (Array<kotlin.js.JsAny?>) -> String
