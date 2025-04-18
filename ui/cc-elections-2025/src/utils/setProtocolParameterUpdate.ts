/**
 * Sets the value of a protocol parameter update using the provided key and value.
 * If the value is not undefined, it calls the corresponding setter function
 *  on the protocolParameterUpdate object.
 * @param protocolParameterUpdate - The protocol parameter update object.
 * @param key - The key of the parameter to update.
 * @param value - The new value for the parameter.
 */
export function setProtocolParameterUpdate<P, V>(
  protocolParameterUpdate: P | { [key: string]: (value: V) => void },
  key: string,
  value: V,
) {
  if (value !== undefined) {
    const snakeCaseKey = key.replace(/([A-Z])/g, "_$1").toLowerCase();
    const setterName = `set_${snakeCaseKey}`;

    if (
      (protocolParameterUpdate as { [key: string]: (value: V) => void })[
        setterName
      ] !== undefined
    ) {
      (protocolParameterUpdate as { [key: string]: (value: V) => void })[
        setterName
      ](value);
    }
  }
}
