/*
 * Copyright (c) 2007 Henri Sivonen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation 
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the 
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in 
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL 
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER 
 * DEALINGS IN THE SOFTWARE.
 */

package nu.validator.saxtree;

import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public final class Element extends ParentNode {

    private final String uri;

    private final String localName;

    private final String qName;

    private final Attributes atts;

    private final List<PrefixMapping> prefixMappings;

    public Element(Locator locator, String uri, String localName, String qName,
            Attributes atts, boolean retainAttributes,
            List<PrefixMapping> prefixMappings) {
        super(locator);
        this.uri = uri;
        this.localName = localName;
        this.qName = qName;
        if (retainAttributes) {
            this.atts = atts;
        } else {
            this.atts = new AttributesImpl(atts);
        }
        this.prefixMappings = prefixMappings;
    }

    @Override
    void visit(TreeParser treeParser) throws SAXException {
        if (prefixMappings != null) {
            for (PrefixMapping mapping : prefixMappings) {
                treeParser.startPrefixMapping(mapping.getPrefix(),
                        mapping.getUri(), this);
            }
        }
        treeParser.startElement(uri, localName, qName, atts, this);
    }

    /**
     * @throws SAXException
     * @see nu.validator.saxtree.Node#revisit(nu.validator.saxtree.TreeParser)
     */
    @Override
    void revisit(TreeParser treeParser) throws SAXException {
        treeParser.endElement(uri, localName, qName, endLocator);
        if (prefixMappings != null) {
            for (PrefixMapping mapping : prefixMappings) {
                treeParser.endPrefixMapping(mapping.getPrefix(), endLocator);
            }
        }
    }

}