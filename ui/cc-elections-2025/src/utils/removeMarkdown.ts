export const removeMarkdown = (markdown?: string | number) => {
  if (!markdown) return "";

  return String(markdown)
    .replace(/(\*\*|__)(.*?)\1/g, "$2")
    .replace(/(\*|_)(.*?)\1/g, "$2")
    .replace(/~~(.*?)~~/g, "$1")
    .replace(/!\[.*?\]\(.*?\)/g, "")
    .replace(/\[(.*?)\]\(.*?\)/g, "$1")
    .replace(/`{1,2}([^`]+)`{1,2}/g, "$1")
    .replace(/^\s{0,3}>\s?/g, "")
    .replace(/^\s{1,3}([-*+]|\d+\.)\s+/g, "")
    .replace(
      /^(\n)?\s{0,}#{1,6}\s*( (.+))? +#+$|^(\n)?\s{0,}#{1,6}\s*( (.+))?$/gm,
      "$1$3$4$6",
    )
    .replace(/\n{2,}/g, "\n")
    .replace(/([\\`*{}[\]()#+\-.!_>])/g, "$1");
};
