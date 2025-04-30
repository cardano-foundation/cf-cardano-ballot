export const geographicRepresentationList = () => {
  const list = [
    "Africa",
    "Asia",
    "Australia and New Zealand",
    "Caribbean",
    "Central America",
    "Central Asia",
    "Eastern Africa",
    "Eastern Asia",
    "Eastern Europe",
    "Europe",
    "Melanesia",
    "Micronesia",
    "Middle Africa",
    "North America",
    "Northern Africa",
    "Northern Europe",
    "Oceania",
    "Polynesia",
    "South America",
    "South-eastern Asia",
    "Southern Africa",
    "Southern Asia",
    "Southern Europe",
    "Western Africa",
    "Western Asia",
    "Western Europe",
    "Worldwide"
  ]
  return list.map(item => ({ value: item, label: item }));
}
