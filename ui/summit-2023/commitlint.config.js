module.exports = {
  extends: ['@commitlint/config-conventional'],
  rules: {
    'type-enum': [
      2,
      'always',
      [
        // https://github.com/angular/angular/blob/22b96b9/CONTRIBUTING.md#-commit-message-guidelines
        'build',
        'ci',
        'docs',
        'feat',
        'fix',
        'perf',
        'refactor',
        'style',
        'test',
        // Additional types
        'chore',
        'deploy',
        'infra',
      ],
    ],
    'scope-enum': [2, 'always', ['all', 'cip-1694', 'backend-services']],
    'scope-empty': [0, 'never'],
  },
  plugins: [
    {
      rules: {
        'scope-empty': (ctx) => {
          if (ctx.scope === null) {
            return [0, 'always'];
          }
          return [2, 'never'];
        },
      },
    },
  ],
};
