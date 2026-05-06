CREATE TABLE message_reactions (
    message_id UUID NOT NULL,
    user_id UUID NOT NULL,
    reaction_type TEXT NOT NULL,
    chat_id UUID,
    created_at TIMESTAMPTZ DEFAULT now() NOT NULL,
    UNIQUE(message_id, user_id)
);

ALTER TABLE message_reactions ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Anyone can select message reactions"
    ON message_reactions FOR SELECT
    USING (true);

CREATE POLICY "Authenticated users can insert their own reactions"
    ON message_reactions FOR INSERT
    WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Authenticated users can update their own reactions"
    ON message_reactions FOR UPDATE
    USING (auth.uid() = user_id);

CREATE POLICY "Authenticated users can delete their own reactions"
    ON message_reactions FOR DELETE
    USING (auth.uid() = user_id);

CREATE OR REPLACE FUNCTION toggle_message_reaction(p_message_id UUID, p_user_id UUID, p_reaction_type TEXT, p_chat_id UUID DEFAULT NULL)
RETURNS void
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM message_reactions
        WHERE message_id = p_message_id AND user_id = p_user_id AND reaction_type = p_reaction_type
    ) THEN
        DELETE FROM message_reactions
        WHERE message_id = p_message_id AND user_id = p_user_id AND reaction_type = p_reaction_type;
    ELSE
        INSERT INTO message_reactions (message_id, user_id, reaction_type, chat_id)
        VALUES (p_message_id, p_user_id, p_reaction_type, p_chat_id)
        ON CONFLICT (message_id, user_id) DO UPDATE SET reaction_type = p_reaction_type;
    END IF;
END;
$$;
