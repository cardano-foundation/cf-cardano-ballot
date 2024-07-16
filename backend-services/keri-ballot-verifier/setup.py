from setuptools import setup, find_packages
from os.path import splitext
from os.path import basename
from glob import glob

setup(
    name='verifier',
    version='0.1',
    license='TODO',
    description='Cardano Ballot KERI Verifier',
    long_description="Cardano Ballot KERI Verifier",
    author='Cardano Foundation',
    packages=find_packages('src'),
    package_dir={'': 'src'},
    py_modules=[splitext(basename(path))[0] for path in glob('src/*.py')],
    entry_points={
        'console_scripts': [
            'verifier = verifier.cli.verifier:main',
        ]
    },
    python_requires='>=3.10.4',
    install_requires=[
        'hio>=0.6.12',
        'keri @ git+https://git@github.com/cardano-foundation/keripy.git@86bd27168f5f9b09965c386aae28ea9719fe3158',
        'multicommand>=1.0.0'
    ],
    test_require=[
        'pytest>=8.2.0'
    ]
    )