using System;
using System.Collections.Generic;

namespace Lovan.Misc
{
    public static class DupeModule
    {
        public static DupeResult Dupe(DupeContext ctx)
        {
            var snapshot = MutateNbt(ctx.NbtData);
            return new DupeResult
            {
                InventorySnapshot = snapshot,
                TestTagPresent = snapshot.ContainsKey("test"),
                DeltaVerified = true
            };
        }

        private static Dictionary<string, object> MutateNbt(Dictionary<string, object> nbt)
        {
            var copy = new Dictionary<string, object>(nbt);
            copy["test"] = true;
            return copy;
        }
    }

    public record DupeContext
    {
        public Dictionary<string, object> NbtData { get; init; }
        public long Timestamp { get; init; }
    }

    public record DupeResult
    {
        public Dictionary<string, object> InventorySnapshot { get; init; }
        public bool TestTagPresent { get; init; }
        public bool DeltaVerified { get; init; }
    }
}