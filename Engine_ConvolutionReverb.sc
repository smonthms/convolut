Engine_ConvolutionReverb : CroneEngine {
  var irBuffer, size = 1.0, dryWet = 0.5;

  init {
    irBuffer = Buffer.alloc(s, 1);
  }

  alloc {
    irBuffer.alloc(44100, 2); // Example allocation size
  }

  set_ir(path) {
    if (File.exists(path).not) { ^this }
    s.sync;
    irBuffer.read(path, 0, -1, 0, true, { |buffer|
      s.sync;
      SynthDef(\convolution_reverb, { |inbus = 0, outbus = 0, size = 1.0, dry_wet = 0.5|
        var input = In.ar(inbus, 2);
        var wet = Convolution2.ar(input, buffer, 2048) * size;
        var dry = input * (1 - dry_wet);
        Out.ar(outbus, wet + dry);
      }).add;
    });
  }

  set_size(val) {
    size = val;
  }

  set_dry_wet(val) {
    dryWet = val;
  }
}

s.waitForBoot {
  Ndef(\convolution_reverb, {
    var input = SoundIn.ar(0!2);
    var output = \convolution_reverb.irBuffer.play(input);
    Out.ar(0, output);
  }).play;
}
