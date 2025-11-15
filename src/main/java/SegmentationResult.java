import java.util.List;

// Класс для хранения результата сегментации и его "стоимости" (штрафа)
class SegmentationResult {
    final List<String> words;
    final int penalty;

    public SegmentationResult(List<String> words, int penalty) {
        this.words = words;
        this.penalty = penalty;
    }
}