package com.financetrackingbackend.dao;

import com.example.model.Quote;
import com.example.model.SymbolLookupResponse;

public interface FinnHub {
    Quote getQuote(String symbol);
    SymbolLookupResponse lookupSymbol(String symbol);
}
