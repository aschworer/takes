/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.takes.tk;

import java.io.IOException;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;

/**
 * Decorator TkRetry, which will not fail immediately on IOException, but
 * will retry a few times.
 *
 * @author Aygul Schworer (agul.schworer@gmail.com)
 *
 * @version $Id$
 *
 */
public final class TkRetry implements Take {

    /**
     * How many times to retry, maximum.
     */
    private final transient Integer count;
    /**
     * Initial delay between retries, in milliseconds.
     */
    private final transient Integer delay;

    /**
     * Original Take.
     */
    private final transient Take take;

    /**
     * Constructor.
     *
     * @param retry Number of tries
     * @param retrydelay Between tries
     * @param originaltake Original take
     */
    public TkRetry(final Integer retry, final Integer
            retrydelay, final Take originaltake) {
        this.count = retry;
        this.delay = retrydelay;
        this.take = originaltake;
    }

    /**
     * Added retrying logic.
     *
     * @param req Request to process
     * @return
     * @throws IOException
     */
    @Override
    public Response act(final Request req) throws IOException {
        int attempts = 0;
        IOException exeption = new IOException();
        while (attempts < this.count) {
            try {
                return this.take.act(req);
            } catch (final IOException ex) {
                attempts = attempts + 1;
                exeption = ex;
                this.sleep();
            }
        }
        throw exeption;
    }

    /**
     * Sleep.
     */
    private void sleep() {
        try {
            Thread.sleep(this.delay);
        } catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(ex);
        }
    }

}
