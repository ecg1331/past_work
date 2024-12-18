# Social Network Analysis of Senate Voting Patterns

This repository contains my final project for **Social Network Analysis (Fall 2024)**. The project explores political polarization within the 118th U.S. Senate through a network analysis of voting patterns.

## Project Overview
- Constructed a weighted network of 103 senators using voting records from Voteview.org.
- Edges represent the frequency of agreement on legislative measures.
- Nodes include biographical information such as party affiliation and state representation.
- Analysis includes calculations of network centrality, modularity, assortativity, and other measures to reveal voting patterns, community structures, and party dynamics.
- Visualizations highlight community structures, intra party dynamics, and polarization within the Senate.

## Files in This Repository
- **`final.Rmd`**: The R Markdown file containing the full analysis, including data preprocessing, calculations, and visualizations.
- **`final.pdf`**: A polished PDF report summarizing the analysis and key findings.
- **`creatingDataset.ipynb`**: Jupyter Notebook detailing the process of constructing the network from raw data.
- **`current_sen.csv`**: Constructed node data with attributes like senator biographical details and party affiliation.
- **`edge_list.csv`**: Constructed list representing the weighted connections between senators.
- **`HSall_member.csv`**, **`S118_votes.csv`**: Original datasets sourced for the analysis.
