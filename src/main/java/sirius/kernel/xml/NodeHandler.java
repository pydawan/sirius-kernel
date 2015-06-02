/*
 * Made with all the love in the world
 * by scireum in Remshalden, Germany
 *
 * Copyright by scireum GmbH
 * http://www.scireum.de - info@scireum.de
 */

package sirius.kernel.xml;

/**
 * Called by the {@link XMLReader} for a parsed sub-DOM tree.
 *
 * @see XMLReader
 */
public interface NodeHandler {

    /**
     * Invoked once a complete subtree was parsed
     *
     * @param node the root node of the subtree parsed by the SAX parser
     */
    void process(StructuredNode node);
}
