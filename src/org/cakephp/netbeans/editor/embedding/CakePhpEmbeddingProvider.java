package org.cakephp.netbeans.editor.embedding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.api.lexer.*;
import org.netbeans.modules.parsing.api.Embedding;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.spi.EmbeddingProvider;
import org.netbeans.modules.parsing.spi.SchedulerTask;
import org.netbeans.modules.parsing.spi.TaskFactory;
import org.netbeans.modules.php.editor.lexer.PHPTokenId;

/**
 * 
 * @author junichi11
 */
public class CakePhpEmbeddingProvider extends EmbeddingProvider{

        @Override
        public List<Embedding> getEmbeddings(Snapshot snpsht) {
                TokenHierarchy<?> th = snpsht.getTokenHierarchy();
                TokenSequence<PHPTokenId> sequence = th.tokenSequence(PHPTokenId.language());
                
                if(sequence == null){
                        return Collections.emptyList();
                }
                sequence.moveStart();
                List<Embedding> embeddings = new ArrayList<Embedding>();
                boolean scriptFlg = false;
                while (sequence.moveNext()){
                        Token<? extends TokenId> t = sequence.token();
                        TokenId id = t.id();
                        String s = id.primaryCategory();
                        if(scriptFlg){
                                if(id == PHPTokenId.T_INLINE_HTML){
                                        embeddings.add(snpsht.create(sequence.offset(), t.length(), "text/javascript")); // NOI18N
                                        scriptFlg = false;
                                }
                        }
                        
                        if(id == PHPTokenId.PHP_STRING){
                                if(TokenUtilities.startsWith(t.text(), "scriptStart")){ // NOI18N
                                        scriptFlg = true;
                                }
                        }
                }
                
                if(embeddings.isEmpty()){
                        return Collections.emptyList();
                }else{
                        return Collections.singletonList(Embedding.create(embeddings));
                }
        }

        @Override
        public int getPriority() {
                return 200;
        }

        @Override
        public void cancel() {
                // do nothing
        }
        
        @MimeRegistration(service = TaskFactory.class, mimeType = "text/x-php5") // NOI18N
        public static final class Factory extends TaskFactory {
                @Override
                public Collection<SchedulerTask> create(final Snapshot snapshot) {
                        return Collections.<SchedulerTask>singletonList(new CakePhpEmbeddingProvider());
                }
        }
}