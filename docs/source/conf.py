# Configuration file for the Sphinx documentation builder.
#
# For the full list of built-in configuration values, see the documentation:
# https://www.sphinx-doc.org/en/master/usage/configuration.html

# -- Project information -----------------------------------------------------
# https://www.sphinx-doc.org/en/master/usage/configuration.html#project-information

project = 'Gitla'
copyright = '2024, Lithikha, Pradeepaa, Ragini, Smriti'
author = 'Lithikha, Pradeepaa, Ragini, Smriti'
release = '1.0'

# -- General configuration ---------------------------------------------------
# https://www.sphinx-doc.org/en/master/usage/configuration.html#general-configuration

extensions = []

templates_path = ['_templates']
exclude_patterns = []



# -- Options for HTML output -------------------------------------------------
# https://www.sphinx-doc.org/en/master/usage/configuration.html#options-for-html-output

html_theme = "sphinx_material"
html_theme_options = {
    "nav_title": "Gitla Documentation",
    "color_primary": "blue",
    "color_accent": "light-blue",
}
html_static_path = ['_static']
