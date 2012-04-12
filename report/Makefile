PAPER_NAME := report

base:
	latex $(PAPER_NAME).tex
	latex $(PAPER_NAME).tex
	dvipdf $(PAPER_NAME).dvi

bib-ps:
	latex $(PAPER_NAME).tex && \
	bibtex $(PAPER_NAME) && \
	latex $(PAPER_NAME).tex && \
	latex $(PAPER_NAME).tex && \
	dvips $(PAPER_NAME).dvi
bib-pdf:
	latex $(PAPER_NAME).tex && \
	bibtex $(PAPER_NAME) && \
	latex $(PAPER_NAME).tex && \
	latex $(PAPER_NAME).tex && \
	dvipdf $(PAPER_NAME).dvi

clean:
	rm -f *.aux *.log *.blg *.bbl *.dvi 
