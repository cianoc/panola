Panola {
	var <parsed_notation;
	var <note_to_midi;
	var <gOCTAVE_DEFAULT;
	var <gOCTAVE_DEFAULT;
    var <gDURATION_DEFAULT;
	var <gMODIFIER_DEFAULT;
	var <gMULTIPLIER_DEFAULT;
	var <gDIVIDER_DEFAULT;
	var <gVOLUME_DEFAULT;
	var <gPLAYDUR_DEFAULT;
	var <gLAG_DEFAULT;

	*new {
		|notation, octave_default="4", dur_default="4", modifier_default="",
		 mult_default="1", div_default="1", vol_default="0.5",
		 playdur_default="0.9", lag_default="0"|

		^super.new.init(notation, octave_default, dur_default, modifier_default, mult_default, div_default, vol_default, playdur_default, lag_default);

	}

	init {
		| notation, octave_default, dur_default, modifier_default,
		  mult_default, div_default, vol_default, playdur_default,
		  lag_default |
		this.init_notation(notation, octave_default, dur_default, modifier_default,
			mult_default, div_default, vol_default, playdur_default, lag_default);
		this.init_midilookup();
	}

	init_notation {
		| notation, octave_default, dur_default, modifier_default,
		  mult_default, div_default, vol_default, playdur_default,
		  lag_default |

		var cOCTAVE_DEFAULT = octave_default;
		var cDURATION_DEFAULT = dur_default;
		var cMODIFIER_DEFAULT = modifier_default;
		var cMULTIPLIER_DEFAULT = mult_default;
		var cDIVIDER_DEFAULT = div_default;
		var cVOLUME_DEFAULT = vol_default;
		var cPLAYDUR_DEFAULT = playdur_default;
		var cLAG_DEFAULT = lag_default;
		var noteletters;

		gOCTAVE_DEFAULT = cOCTAVE_DEFAULT;
		gDURATION_DEFAULT = cDURATION_DEFAULT;
		gMODIFIER_DEFAULT = cMODIFIER_DEFAULT;
		gMULTIPLIER_DEFAULT = cMULTIPLIER_DEFAULT;
		gDIVIDER_DEFAULT = cDIVIDER_DEFAULT;
		gVOLUME_DEFAULT = cVOLUME_DEFAULT;
		gPLAYDUR_DEFAULT = cPLAYDUR_DEFAULT;
		gLAG_DEFAULT = cLAG_DEFAULT;

		if (notation[notation.size-1] != $ ) {
			notation = notation++" ";
		};
		noteletters = [];
		notation.findRegexp("[^\\ ]+(\\ )+").do({
			|x|
			if (x[1].stripWhiteSpace.compare("") != 0) {
				noteletters = noteletters.add(x[1]);
			};
		});
		//noteletters.postln;
		noteletters.do({
			|note|
			var modifier = cMODIFIER_DEFAULT;
			var octave = cOCTAVE_DEFAULT;
			var duration = cDURATION_DEFAULT;
			var multiplier = cMULTIPLIER_DEFAULT;
			var divider = cDIVIDER_DEFAULT;
			var volume = cVOLUME_DEFAULT;
			var playdur = cPLAYDUR_DEFAULT;
			var lag = cLAG_DEFAULT;
			var fullnote = "";
			var letter = note[0];
			var afterletter = note.copyRange(1, note.size-1);
			var modifierregexp = "(#|x|--|-)";
			var octaveregexp = "(\\d+)";
			var durationregexp = "(\\d+)";
			var multiplierregexp = "(\\d+)";
			var dividerregexp = "(\\d+)";
			var propertyregex = "(vol|pdur|lag)";
			var propertytyperegex = "({|\\[)";
			var propertyvalueregex = "[0-9]*\\.?[0-9]+";
			var aftermodifier = "";
			var afteroctave = "";
			var afterduration = "";
			var aftermultiplier = "";
			var afterdivider = "";
			var afterproperty = "";
			var extractedproperty = "";
			var propertytype = "";
			var afterextractedproperty="";
			var propertyvalue = "";
			var props = [];

			aftermodifier = afterletter;
			if (afterletter.findRegexpAt(modifierregexp,0).notNil) {
				modifier = afterletter.findRegexpAt(modifierregexp,0)[0];
				aftermodifier = afterletter.copyRange(afterletter.findRegexpAt(modifierregexp,0)[1], afterletter.size-1);
			};
			afteroctave = aftermodifier;
			if (aftermodifier.findRegexpAt(octaveregexp, 0).notNil) {
				octave = aftermodifier.findRegexpAt(octaveregexp,0)[0];
				cOCTAVE_DEFAULT = octave; // update octave default
				afteroctave = aftermodifier.copyRange(aftermodifier.findRegexpAt(octaveregexp,0)[1], aftermodifier.size-1);
			};
			if (afteroctave[0].notNil && afteroctave[0] == $_) {
				afteroctave = afteroctave.copyRange(1, afteroctave.size-1);
			};
			afterduration = afteroctave;
			if (afteroctave.findRegexpAt(durationregexp, 0).notNil) {
				duration = afteroctave.findRegexpAt(durationregexp,0)[0];
				cDURATION_DEFAULT = duration; // update duration default, reset multiplier, divider
				cMULTIPLIER_DEFAULT = "1";
				divider = "1";
				cDIVIDER_DEFAULT = "1";
				multiplier = "1";
				afterduration = afteroctave.copyRange(afteroctave.findRegexpAt(durationregexp,0)[1], afteroctave.size-1);
			};
			if (afterduration[0].notNil && afterduration[0] == $*) {
				afterduration = afterduration.copyRange(1, afterduration.size-1);
				aftermultiplier = afterduration;
				if (afterduration.findRegexpAt(multiplierregexp, 0).notNil) {
					multiplier = afterduration.findRegexpAt(multiplierregexp, 0)[0];
					cMULTIPLIER_DEFAULT = multiplier; // update multiplier default
					aftermultiplier = afterduration.copyRange(afterduration.findRegexpAt(multiplierregexp, 0)[1], afterduration.size-1);
				};
			} {
				aftermultiplier = afterduration;
			};
			if (aftermultiplier[0].notNil && aftermultiplier[0] == $/) {
				aftermultiplier = aftermultiplier.copyRange(1, aftermultiplier.size-1);
				afterdivider = aftermultiplier;
				if (aftermultiplier.findRegexpAt(dividerregexp, 0).notNil) {
					divider = aftermultiplier.findRegexpAt(dividerregexp, 0)[0];
					cDIVIDER_DEFAULT = divider;
					afterdivider = aftermultiplier.copyRange(aftermultiplier.findRegexpAt(dividerregexp, 0)[1], aftermultiplier.size-1);
				};
			} {
				afterdivider = aftermultiplier;
			};

			afterproperty = afterdivider;
			while({afterproperty[0] == $\\}, {
				var type = "fixed";
				var val = "0.5";
				var prop = "vol";
				afterproperty = afterproperty.copyRange(1, afterproperty.size-1);
				if (afterproperty.findRegexpAt(propertyregex, 0).notNil) {
					extractedproperty = afterproperty.findRegexpAt(propertyregex, 0)[0];
					afterextractedproperty = afterproperty.copyRange(afterproperty.findRegexpAt(propertyregex, 0)[1], afterproperty.size-1);
					propertytype = afterextractedproperty.findRegexpAt(propertytyperegex, 0);
					if (propertytype.notNil) {
						prop = extractedproperty;
						if (propertytype[0].compare("{") == 0) {
							type = "anim";
						} {
							type = "fixed";
						};
						afterextractedproperty = afterextractedproperty.copyRange(propertytype[1], afterextractedproperty.size-1);
						val = afterextractedproperty.findRegexpAt(propertyvalueregex, 0);
						props = props.add([prop, type, val[0]]);
						afterproperty = afterextractedproperty.copyRange(val[1]+1, afterextractedproperty.size-1);
					};
				};
			});

			fullnote = if (letter == $r) {letter} {letter++modifier++octave};
			// ("note: "++fullnote).postln;
			// ("dur: " ++ duration ++ "*" ++ multiplier ++ "/" ++ divider).postln;
			// ("props: "++props).postln;
			// ("").postln;

			parsed_notation = parsed_notation.add([fullnote, duration, multiplier, divider, props]);
		});
	}

	init_midilookup {
		var notenum = 0;
		var corner_case_octave_lower;
		var corner_case_octave_higher;
		var chromatic_scale;
		note_to_midi = Dictionary.new;
		chromatic_scale = [["c", "b#", "d--"],  // one row contains all synonyms (i.e. synonym for our purpose)
			["c#", "bx", "d-"],
			["d", "cx", "e--"],
			["d#", "e-", "f--"],
			["e", "dx", "f-"],
			["f", "e#", "g--"],
			["f#", "ex", "g-"],
			["g", "fx", "a--"],
			["g#", "a-"],
			["a", "gx", "b--"],
			["a#", "b-", "c--"],
			["b", "ax", "c-"]];
		corner_case_octave_lower = Set["b#", "bx"];
		corner_case_octave_higher = Set["c-", "c--"];

		11.do({
			| octave |
			chromatic_scale.do({
				| synonyms |
				synonyms.do({
					| note |
					var o = octave - 1;
					if (corner_case_octave_lower.includes(note),{
						o = o - 1;
					}, {
						if (corner_case_octave_higher.includes(note), {
							o = o + 1
						})
					});
					note_to_midi[note++o.asString] = notenum;
				});
				notenum = notenum+1;
			});
		});

		note_to_midi["r"] = Rest();
	}

	notationnotePattern {
		var notelist = parsed_notation.collect({
			| el |
			el[0];
		});
		^Pseq(notelist, 1);
	}

	midinotePattern {
		var notelist = parsed_notation.collect({
			| el |
			note_to_midi[el[0].asString];
		});
		^Pseq(notelist, 1);
	}

	notationdurationPattern {
		var durlist = parsed_notation.collect({
			| el |
			var duration = el[1];
			var multiplier = el[2];
			var divider = el[3];
			"(1/"++duration++")*("++multiplier++"/"++divider++")";
		});
		^Pseq(durlist, 1);
	}

	durationPattern {
		var durlist = parsed_notation.collect({
			| el |
			var duration = el[1];
			var multiplier = el[2];
			var divider = el[3];
			(1/duration.asFloat)*(multiplier.asFloat/divider.asFloat);
		});
		^Pseq(durlist, 1);
	}

	pr_animatedPattern {
		| prop_name="vol", default_type = "fixed", default_propval = 0.5 |
		var currval = default_propval;
		var patlist = [];
		// extract only properties
		var proplist = parsed_notation.collect({
			| el |
			el[4];
		});
		// keep only volume properties + add distance between current and previous volume property spec
		var volprops = [];
		var distance = 0;
		var clumped = [];
		var clumpedsize = 0;
		proplist.do({
			|propsfornote|
			var foundVol = false;
			propsfornote.do({
				|singleprop|
				if (singleprop[0].compare(prop_name) == 0) {
					var copyprop = singleprop.copy();
					distance = distance + 1;
					copyprop = copyprop.add(distance);
					volprops = volprops.add(copyprop);
					currval = singleprop[2];
					foundVol = true;
					distance = 0;
				};
			});
			if (foundVol.not) {
				//volprops = volprops.add([]);
				distance = distance + 1;
			};
		});
		if (distance != 0) {
			volprops = volprops.add([prop_name, "fixed", currval, distance]);
		};
		// now turn into patterns
		clumped = volprops.slide(2, 1).clump(2);
		clumpedsize = clumped.size;
		if (clumped.size == 0) {
			patlist = patlist.add(Pseq([default_propval.asFloat], proplist.size));
		} {
			if (clumped[0][0][3].asInteger != 1) {
				patlist = patlist.add(Pseq([default_propval.asFloat], clumped[0][0][3].asInteger-1));
			};
			clumped.do({
				| pair, idx |
				var type = pair[0][1];
				var beginval = pair[0][2].asFloat;
				var endval = pair[1][2].asFloat;
				var length = pair[1][3].asInteger;
				var number = length;
				if (idx == (clumped.size-1)) {
					number = number + 1;
				};
				if (type.compare("anim") == 0) {
					patlist = patlist.add(Pseries(beginval, ((endval - beginval)/(length)), number));
				} {
					patlist = patlist.add(Pseq([beginval], number));
				};
			});
		}

		^Pseq(patlist, 1);
	}

	volumePattern {
		^this.pr_animatedPattern("vol", "fixed", gVOLUME_DEFAULT);
	}

	lagPattern {
		^this.pr_animatedPattern("lag", "fixed", gLAG_DEFAULT);
	}

	pdurPattern {
		^this.pr_animatedPattern("pdur", "fixed", gPLAYDUR_DEFAULT);
	}


}