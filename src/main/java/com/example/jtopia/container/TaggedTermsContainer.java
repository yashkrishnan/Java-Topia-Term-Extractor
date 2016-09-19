package com.example.jtopia.container;

import java.util.ArrayList;
import java.util.List;

/**
 * Tagged terms container class
 */

public class TaggedTermsContainer {
    public List<TaggedTerms> taggedTerms = new ArrayList<TaggedTerms>();

    public void addTaggedTerms(String term, String tag, String norm) {
        taggedTerms.add(new TaggedTerms(term, tag, norm));
    }
}
